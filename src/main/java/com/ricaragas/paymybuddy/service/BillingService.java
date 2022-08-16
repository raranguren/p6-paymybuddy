package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.model.BillingDetails;
import com.ricaragas.paymybuddy.model.Invoice;

import java.util.Optional;

public interface BillingService {

    String getUrlForTransaction(Invoice invoice);

    Optional<Invoice> getInvoiceIfTransactionSuccessful(String transactionId);

    Invoice getInvoiceForMoneyChargeUp(int amountInCents, BillingDetails billingDetails);

    Invoice getInvoiceForMoneyWithdrawal(int amountInCents, BillingDetails billingDetails);

}
