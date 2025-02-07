package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.configuration.WebConfig;
import com.ricaragas.paymybuddy.controller.SimulatedBankController;
import com.ricaragas.paymybuddy.model.BillingDetails;
import com.ricaragas.paymybuddy.dto.InvoiceDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static java.util.Optional.empty;

@Service
@Log4j2
public class BillingServiceImpl implements BillingService{

    private final ArrayList<InvoiceDTO> invoicesForTransactions = new ArrayList<>();
    private final ArrayList<Runnable> onSuccessCallbackDelegates = new ArrayList<>();
    private final ArrayList<Runnable> onCancelCallbackDelegates = new ArrayList<>();
    private final HashMap<Integer, Boolean> transactionResults = new HashMap<>();

    @Override
    public InvoiceDTO getInvoiceForMoneyChargeUp(int amountInCents, BillingDetails billingDetails) {
        var invoice = new InvoiceDTO();
        invoice.setTransferInCents(amountInCents);
        invoice.setFeeInCents(amountInCents * 5 / 1000);
        invoice.setVatInCents(invoice.getFeeInCents() * 20 / 100);
        return invoice;
    }

    @Override
    public InvoiceDTO getInvoiceForMoneyWithdrawal(int amountInCents, BillingDetails billingDetails) {
        var invoice = new InvoiceDTO();
        invoice.setTransferInCents(-amountInCents);
        invoice.setFeeInCents(amountInCents * 5 / 1000);
        invoice.setVatInCents(invoice.getFeeInCents() * 20 / 100);
        return invoice;
    }

    @Override
    public String getUrlAndBeginTransaction(InvoiceDTO invoice,
                                            Runnable onStart, Runnable onSuccess, Runnable onCancel) {
        var transactionId = invoicesForTransactions.size();
        invoicesForTransactions.add(invoice);
        onSuccessCallbackDelegates.add(onSuccess);
        onCancelCallbackDelegates.add(onCancel);

        onStart.run();
        if (invoice.getTotalInCents() < 0) {
            finishSimulatedTransaction(String.valueOf(transactionId), true);
        }
        return SimulatedBankController.URL
                + "?simulatedPayment=" + transactionId
                + "&ref=" + WebConfig.URL_CALLBACK_FROM_BANK
                + "?transactionId=" + transactionId;
    }

    @Override
    public boolean isTransactionSuccessful(String transactionId) {
        var index =  getIndexForTransactionId(transactionId);
        if (index.isEmpty()) return false;
        return getResult(index.get()).orElse(false);
    }

    // SIMULATION

    public void finishSimulatedTransaction(String transactionId, boolean success) {
        int index = getIndexForTransactionId(transactionId).orElseThrow();
        if (getResult(index).isPresent()) {
            log.info("SIMULATED BANK - ERROR, can't repeat the same transaction");
            return;
        }
        var invoice = getSavedInvoice(index).orElseThrow();
        transactionResults.put(index, success);
        if (success) {
            log.info("SIMULATED BANK - transaction successful - Amount = {} €", invoice.getTotalInEuros());
            onSuccessCallbackDelegates.get(index).run();
        } else {
            log.info("SIMULATED BANK - transaction cancelled");
            onCancelCallbackDelegates.get(index).run();
        }
    }

    // UTILS

    private Optional<InvoiceDTO> getSavedInvoice(int index) {
        if (index >= invoicesForTransactions.size() || index < 0) {
            return empty();
        }
        return Optional.of(invoicesForTransactions.get(index));
    }

    private Optional<Integer> getIndexForTransactionId(String transactionId) {
        int index;
        try {
            index = Integer.parseInt(transactionId);
        } catch(NumberFormatException e) {
            return empty();
        }
        return Optional.of(index);
    }

    private Optional<Boolean> getResult(int index) {
        return Optional.ofNullable(transactionResults.get(index));
    }

}
