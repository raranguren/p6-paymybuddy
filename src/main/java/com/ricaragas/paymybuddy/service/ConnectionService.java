package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.model.Connection;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.ConnectionRepository;
import com.ricaragas.paymybuddy.dto.TransferRowDTO;
import com.ricaragas.paymybuddy.exceptions.IsSameUserException;
import com.ricaragas.paymybuddy.exceptions.TextTooShortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ConnectionService {

    @Autowired
    ConnectionRepository connectionRepository;

    @Autowired
    TransferService transferService;

    public void save(Wallet creator, Wallet target, String name) throws IsSameUserException, TextTooShortException {
        if (name == null || "".equals(name)) throw new TextTooShortException();
        if (creator.equals(target)) throw new IsSameUserException();

        var connection = new Connection();
        connection.setCreator(creator);
        connection.setTarget(target);
        connection.setName(name);
        connectionRepository.save(connection);
    }

    public Optional<Connection> find(Wallet creator, Wallet target) {
        return connectionRepository.findByCreatorAndTarget(creator, target);
    }

    public Optional<Connection> findById(Wallet sender, Long receiverConnectionId) {
        return sender.getConnections().stream()
                .filter(connection -> connection.getId().equals(receiverConnectionId))
                .findFirst();
    }

    public List<TransferRowDTO> getTransferRows(Wallet wallet) {
        var connections = wallet.getConnections();
        var result = new ArrayList<TransferRowDTO>();
        for (var connection : connections) {
            result.addAll(transferService.getTransferRows(connection));
        }
        return result;
    }

    public void saveTransfer(Connection connection, String description, int amountInCents) {
        transferService.save(connection, description, amountInCents);
    }
}
