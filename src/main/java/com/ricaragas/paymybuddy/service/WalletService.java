package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.model.Invoice;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.WalletRepository;
import com.ricaragas.paymybuddy.service.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.hibernate.Hibernate.initialize;

@Service
public class WalletService {

    @Autowired
    UserService userService;
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    TransferService transferService;
    @Autowired
    BillingService billingService;

    @Transactional
    public Wallet getWalletForAuthenticatedUser() {
        var wallet = nonTransactionalGetWalletForAuthenticatedUser();
        initialize(wallet.getConnections());
        initialize(wallet.getSentTransfers());
        return wallet;
    }

    private Wallet nonTransactionalGetWalletForAuthenticatedUser() {
        var user = userService.getAuthenticatedUser();
        if (user.isEmpty()) throw new NotAuthenticated();
        return user.get().getWallet();
    }

    @Transactional
    public void addConnection(String email) throws IsCurrentUser, NotFound, IsDuplicated {
        var currentWallet = nonTransactionalGetWalletForAuthenticatedUser();
        if (email.equals(currentWallet.getUser().getEmail()))
            throw new IsCurrentUser();
        var userToAdd = userService.findByEmail(email);
        if (userToAdd.isEmpty()) throw new NotFound();
        var connections = currentWallet.getConnections();
        var newConnection = userToAdd.get().getWallet();
        if (connections.contains(newConnection)) throw new IsDuplicated();
        connections.add(newConnection);
        walletRepository.save(currentWallet);
    }

    @Transactional
    public void pay(Long receiverConnectionId, String description, double amountInEuros)
            throws NotFound, TextTooShort, NotEnoughBalance, InvalidAmount {
        var sender = nonTransactionalGetWalletForAuthenticatedUser();

        if (description == null || "".equals(description)) throw new TextTooShort();

        int amountInCents = Math.toIntExact(Math.round(amountInEuros * 100));
        if (amountInCents <= 0) throw new InvalidAmount();

        if (sender.getBalanceInCents() < amountInCents) throw new NotEnoughBalance();

        var connectionFound = sender.getConnections().stream()
                .filter(wallet -> wallet.getId().equals(receiverConnectionId))
                .findFirst();
        if (connectionFound.isEmpty()) throw new NotFound();

        var receiver = connectionFound.get();
        sender.setBalanceInCents(sender.getBalanceInCents() - amountInCents);
        receiver.setBalanceInCents(receiver.getBalanceInCents() + amountInCents);
        transferService.save(sender, receiver, description, amountInCents);
        walletRepository.save(sender);
        walletRepository.save(receiver);
    }

    @Transactional
    public Invoice getInvoiceToAddAmount(Double amountToAddInEuros) throws InvalidAmount {
        if (amountToAddInEuros == null || amountToAddInEuros < 0.01) {
            throw new InvalidAmount();
        }
        int amountInCents = (int) (amountToAddInEuros * 100.0);
        var billingDetails = nonTransactionalGetWalletForAuthenticatedUser().getBillingDetails();
        return billingService.getInvoiceForMoneyChargeUp(amountInCents, billingDetails);
    }

    @Transactional
    public String getUrlToAddMoney(Invoice invoice) {
        return billingService.getUrlToBeginTransaction(invoice,
                ()-> doAddMoney(invoice));
    }

    @Transactional
    public void doAddMoney(Invoice invoice) {
        var wallet = nonTransactionalGetWalletForAuthenticatedUser();
        var newBalance = wallet.getBalanceInCents() + invoice.getTransferInCents();
        wallet.setBalanceInCents(newBalance);
    }

    @Transactional
    public boolean isTransactionSuccessful(String transactionId) {
        return billingService.isTransactionSuccessful(transactionId);
    }
}
