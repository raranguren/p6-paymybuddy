package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.model.Connection;
import com.ricaragas.paymybuddy.model.Transfer;
import com.ricaragas.paymybuddy.repository.TransferRepository;
import com.ricaragas.paymybuddy.service.dto.TransferRowDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferService {

    @Autowired
    TransferRepository transferRepository;

    @Transactional
    public void save(Connection connection, String description, int amountInCents) {
        var transfer = new Transfer();
        transfer.setConnection(connection);
        transfer.setAmountInCents(amountInCents);
        transfer.setDescription(description);
        transfer.setTimeCompleted(new Timestamp(System.currentTimeMillis()));
        transferRepository.save(transfer);
    }

    public List<TransferRowDTO> getTransferRows(Connection connection) {
        var name = connection.getName();
        return connection.getTransfers().stream()
                .map(transfer -> new TransferRowDTO(
                        name,
                        transfer.getDescription(),
                        transfer.getAmountInEuros(),
                        transfer.getTimeCompleted()))
                .collect(Collectors.toList());
    }

}
