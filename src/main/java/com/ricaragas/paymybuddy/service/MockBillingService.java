package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.controller.WebController;
import com.ricaragas.paymybuddy.model.BillingDetails;
import com.ricaragas.paymybuddy.model.Invoice;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static java.util.Optional.empty;

@Service
@Log4j2
public class MockBillingService implements BillingService{

    private final ArrayList<Invoice> mockInvoices = new ArrayList<>();
    private final ArrayList<Runnable> callbackDelegates = new ArrayList<>();
    private final HashMap<Integer, Boolean> transactionResults = new HashMap<>();

    @Override
    public Invoice getInvoiceForMoneyChargeUp(int amountInCents, BillingDetails billingDetails) {
        var invoice = new Invoice();
        invoice.setTransferInCents(amountInCents);
        invoice.setFeeInCents(amountInCents * 5 / 1000);
        invoice.setVatInCents(invoice.getFeeInCents() * 20 / 100);
        return invoice;
    }

    @Override
    public Invoice getInvoiceForMoneyWithdrawal(int amountInCents, BillingDetails billingDetails) {
        var invoice = new Invoice();
        invoice.setTransferInCents(-amountInCents);
        invoice.setFeeInCents(amountInCents * 5 / 1000);
        invoice.setVatInCents(invoice.getFeeInCents() * 20 / 100);
        return invoice;
    }

    @Override
    public String getUrlToBeginTransaction(Invoice invoice, Runnable callbackOnSuccess) {
        var transactionId = mockInvoices.size();
        mockInvoices.add(invoice);
        callbackDelegates.add(callbackOnSuccess);
        return "/mock-bank?mockPayment=" + transactionId
                + "&ref=" + WebController.URL_CALLBACK_FROM_BANK
                + "?transactionId=" + transactionId;
    }

    @Override
    public boolean isTransactionSuccessful(String transactionId) {
        var index =  getIndex(transactionId);
        if (index.isEmpty()) return false;
        return isSuccessful(index.get());
    }

    // TO INVOKE FROM MOCK BANK

    public void finishTransaction(String transactionId, boolean success) {
        int index = getIndex(transactionId).orElseThrow();
        var invoice = getInvoice(index).orElseThrow();
        setResult(index, success);
        if (success) {
            log.info("MOCK BANK - transaction successful - Amount = {} €", invoice.getTotalInEuros());
            callbackDelegates.get(index).run();
        }
    }

    // UTILS

    private Optional<Invoice> getInvoice(int index) {
        if (index >= mockInvoices.size() || index < 0) {
            return empty();
        }
        return Optional.of(mockInvoices.get(index));
    }

    private Optional<Integer> getIndex(String transactionId) {
        int index;
        try {
            index = Integer.parseInt(transactionId);
        } catch(NumberFormatException e) {
            return empty();
        }
        return Optional.of(index);
    }

    private void setResult(int index, boolean success) {
        transactionResults.put(index, success);
    }

    private boolean isSuccessful(int index) {
        return transactionResults.get(index);
    }

}
