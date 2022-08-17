package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.model.BillingDetails;
import com.ricaragas.paymybuddy.model.Invoice;

public interface BillingService {

    String getUrlToBeginTransaction(Invoice invoice, Runnable callbackOnSuccess);

    boolean isTransactionSuccessful(String transactionId);

    Invoice getInvoiceForMoneyChargeUp(int amountInCents, BillingDetails billingDetails);

    Invoice getInvoiceForMoneyWithdrawal(int amountInCents, BillingDetails billingDetails);

}
