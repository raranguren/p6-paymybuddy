package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.model.BillingDetails;
import com.ricaragas.paymybuddy.model.Invoice;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Log4j2
public class MockBillingService implements BillingService{

    private final ArrayList<Invoice> fakeTransactions = new ArrayList<>();

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
    public String getUrlForTransaction(Invoice invoice) {
        var transactionId = fakeTransactions.size();
        fakeTransactions.add(invoice);
        return "/mock-bank?mockPayment=" + transactionId + "&ref=/add-balance?transactionId=" + transactionId;
    }

    @Override
    public Optional<Invoice> getInvoiceIfTransactionSuccessful(String transactionId) {
        int index;
        try {
            index = Integer.parseInt(transactionId);
        } catch(NumberFormatException e)  {
            return Optional.empty();
        }
        if(index >= fakeTransactions.size() || index < 0){
            return Optional.empty();
        }
        var invoice = fakeTransactions.get(index);
        log.info("MOCK BILLING SERVICE: Amount charged: {} €", invoice.getTotalInEuros());
        return Optional.of(invoice);
    }

}
