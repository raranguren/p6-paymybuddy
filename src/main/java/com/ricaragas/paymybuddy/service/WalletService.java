package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.dto.InvoiceDTO;
import com.ricaragas.paymybuddy.exceptions.*;
import com.ricaragas.paymybuddy.model.User;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final BillingService billingService;
    private final PrincipalService principalService;
    WalletService(WalletRepository walletRepository, BillingService billingService, PrincipalService principalService) {
        this.walletRepository = walletRepository;
        this.billingService = billingService;
        this.principalService = principalService;
    }

    public Wallet getActiveWallet() {
        return findByEmail(principalService.getEmail())
                .orElseThrow(NotAuthenticatedException::new);
    }

    public Optional<Wallet> findByEmail(String email) {
        return walletRepository.findByUser_email(email);
    }

    public void createWallet(User user) {
        var wallet = new Wallet();
        wallet.setUser(user);
        walletRepository.save(wallet);
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

    public void startBalanceWithdrawal(InvoiceDTO invoice, int balanceConfirmationInCents) throws InvalidAmountException {
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

    public void doBalanceUpdate(Wallet wallet, int balanceDifferenceInCents) throws NotEnoughBalanceException {
        var newBalance = wallet.getBalanceInCents() + balanceDifferenceInCents;
        if (newBalance < 0) throw new NotEnoughBalanceException();
        wallet.setBalanceInCents(newBalance);
        walletRepository.save(wallet);
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

    public Double getBalanceInEuros(String email) throws NotFoundException {
        var wallet = walletRepository.findByUser_email(email)
                .orElseThrow(NotFoundException::new);
        return wallet.getBalanceInEuros();
    }

}
