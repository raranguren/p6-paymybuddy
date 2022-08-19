package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.model.BillingDetails;
import com.ricaragas.paymybuddy.service.dto.InvoiceDTO;

public interface BillingService {

    String getUrlToBeginTransaction(InvoiceDTO invoice, Runnable callbackOnSuccess);

    boolean isTransactionSuccessful(String transactionId);

    InvoiceDTO getInvoiceForMoneyChargeUp(int amountInCents, BillingDetails billingDetails);

    InvoiceDTO getInvoiceForMoneyWithdrawal(int amountInCents, BillingDetails billingDetails);

}
