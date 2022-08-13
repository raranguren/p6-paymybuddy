package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.model.BillingDetails;

import java.util.Optional;

public interface BillingService {

    Optional<String> getUrlForPayment(BillingDetails billingDetails, int amountInCents);

    Optional<Integer> completeAndConfirmAmountBilledInCents(String transactionId);

}
