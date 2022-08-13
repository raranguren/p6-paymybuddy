package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.model.BillingDetails;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Log4j2
public class FakeBillingService implements BillingService{

    private final ArrayList<Integer> fakeTransactions = new ArrayList<>();

    @Override
    public Optional<String> getUrlForPayment(BillingDetails billingDetails, int amountInCents) {
        var transactionId = fakeTransactions.size();
        fakeTransactions.add(amountInCents);
        var url = "/fake-bank?transaction=" + transactionId;
        return Optional.of(url);
    }

    @Override
    public Optional<Integer> completeAndConfirmAmountBilledInCents(String transactionId) {
        int index;
        try {
            index = Integer.parseInt(transactionId);
        } catch(NumberFormatException e)  {
            return Optional.empty();
        }
        if(index >= fakeTransactions.size() || index < 0){
            return Optional.empty();
        }
        var amountBilledInCents = fakeTransactions.get(index);
        log.info("Amount billed: {} cents", amountBilledInCents);
        return Optional.of(amountBilledInCents);
    }
}
