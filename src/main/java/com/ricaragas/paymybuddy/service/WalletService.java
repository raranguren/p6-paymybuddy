package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.exceptions.*;
import com.ricaragas.paymybuddy.model.Connection;
import com.ricaragas.paymybuddy.dto.InvoiceDTO;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.WalletRepository;
import com.ricaragas.paymybuddy.dto.TransferRowDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class WalletService {

    @Autowired
    UserService userService;
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    ConnectionService connectionService;
    @Autowired
    BillingService billingService;

    private Wallet getActiveWallet() {
        var user = userService.getAuthenticatedUser();
        if (user.isEmpty()) throw new NotAuthenticatedException();
        return user.get().getWallet();
    }

    public List<TransferRowDTO> getSentTransfersPage(int page, int pageSize) {
        var allItems = connectionService.getTransferRows(getActiveWallet());
        allItems.sort(TransferRowDTO::compareNewerFirst);
        // TODO pages
        return allItems;
    }

    public Map<Long, String> getConnectionOptions() {
        return getActiveWallet()
                .getConnections().stream().collect(
                        Collectors.toMap(
                                Connection::getId,
                                Connection::getName
                        ));
    }

    public void addConnection(String name, String email) throws IsSameUserException, NotFoundException, IsDuplicatedException, TextTooShortException {
        var activeWallet = getActiveWallet();

        var targetUser = userService.findByEmail(email);
        if (targetUser.isEmpty()) throw new NotFoundException();

        var targetWallet = targetUser.get().getWallet();
        var existingConnection = connectionService.find(activeWallet, targetWallet);
        if (existingConnection.isPresent()) throw new IsDuplicatedException();

        connectionService.save(activeWallet, targetWallet, name);
    }

    public void pay(Long receiverConnectionId, String description, double amountInEuros)
            throws NotFoundException, TextTooShortException, NotEnoughBalanceException, InvalidAmountException {

        var sender = getActiveWallet();

        if (description == null || "".equals(description)) throw new TextTooShortException();

        int amountInCents = Math.toIntExact(Math.round(amountInEuros * 100));
        if (amountInCents <= 0) throw new InvalidAmountException();
        if (sender.getBalanceInCents() < amountInCents) throw new NotEnoughBalanceException();

        var connectionFound = connectionService.findById(sender, receiverConnectionId);
        if (connectionFound.isEmpty()) throw new NotFoundException();

        var receiver = connectionFound.get().getTarget();
        sender.setBalanceInCents(sender.getBalanceInCents() - amountInCents);
        receiver.setBalanceInCents(receiver.getBalanceInCents() + amountInCents);
        connectionService.saveTransfer(connectionFound.get(), description, amountInCents);
        walletRepository.save(sender);
        walletRepository.save(receiver);
    }

    public InvoiceDTO getInvoiceToAddAmount(Double amountToAddInEuros) throws InvalidAmountException {
        if (amountToAddInEuros == null || amountToAddInEuros < 0.01) {
            throw new InvalidAmountException();
        }
        int amountInCents = (int) (amountToAddInEuros * 100.0);
        var billingDetails = getActiveWallet().getBillingDetails();
        return billingService.getInvoiceForMoneyChargeUp(amountInCents, billingDetails);
    }

    public InvoiceDTO getInvoiceToWithdrawAll() throws NotEnoughBalanceException {
        var amountInCents = getActiveWallet().getBalanceInCents();
        if (amountInCents == 0) throw new NotEnoughBalanceException();
        var billingDetails = getActiveWallet().getBillingDetails();
        return billingService.getInvoiceForMoneyWithdrawal(amountInCents, billingDetails);
    }

    public String getUrlAndStartAddingMoney(InvoiceDTO invoice) {
        return billingService.getUrlAndBeginTransaction(invoice,
                ()-> {},
                ()-> doBalanceUpdate(invoice),
                ()-> {}
        );
    }

    public void startBalanceWithdrawal(InvoiceDTO invoice, long balanceConfirmationInCents) throws InvalidAmountException {
        var currentBalanceInCents = getActiveWallet().getBalanceInCents();
        if (currentBalanceInCents != balanceConfirmationInCents) throw new InvalidAmountException();
        if (invoice.getTransferInCents() != -balanceConfirmationInCents) throw new InvalidAmountException();

        billingService.getUrlAndBeginTransaction(invoice,
                ()-> doBalanceUpdate(invoice),
                ()-> {},
                ()-> rollbackBalanceUpdate(invoice));
    }

    public void doBalanceUpdate(InvoiceDTO invoice) {
        var activeWallet = getActiveWallet();
        var newBalance = activeWallet.getBalanceInCents() + invoice.getTransferInCents();
        activeWallet.setBalanceInCents(newBalance);
        walletRepository.save(activeWallet);
    }

    public void rollbackBalanceUpdate(InvoiceDTO invoice) {
        var activeWallet = getActiveWallet();
        var newBalance = activeWallet.getBalanceInCents() - invoice.getTransferInCents();
        activeWallet.setBalanceInCents(newBalance);
        walletRepository.save(activeWallet);
    }

    public boolean isTransactionSuccessful(String transactionId) {
        return billingService.isTransactionSuccessful(transactionId);
    }

    public Double getBalanceInEuros() {
        return getActiveWallet()
                .getBalanceInEuros();
    }

    public Object getSentTransfersPageCount() {
        return 1; // TODO implement pagination
    }

}
