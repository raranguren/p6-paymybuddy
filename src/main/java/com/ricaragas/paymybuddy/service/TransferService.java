package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.model.Transfer;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
public class TransferService {

    @Autowired
    TransferRepository transferRepository;

    @Transactional
    public void save(Wallet sender, Wallet receiver, String description, int amountInCents) {
        var transfer = new Transfer();
        transfer.setSender(sender);
        transfer.setReceiver(receiver);
        transfer.setAmountInCents(amountInCents);
        transfer.setDescription(description);
        transfer.setTimeCompleted(new Timestamp(System.currentTimeMillis()));
        transferRepository.save(transfer);
    }

}
