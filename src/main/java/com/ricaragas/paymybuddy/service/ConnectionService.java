package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.exceptions.IsDuplicatedException;
import com.ricaragas.paymybuddy.exceptions.NotFoundException;
import com.ricaragas.paymybuddy.model.Connection;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.ConnectionRepository;
import com.ricaragas.paymybuddy.exceptions.IsSameUserException;
import com.ricaragas.paymybuddy.exceptions.TextTooShortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConnectionService {

    @Autowired
    ConnectionRepository connectionRepository;

    @Autowired
    TransferService transferService;

    @Autowired
    UserService userService;

    public void save(String creatorEmail, String targetEmail, String name)
            throws IsSameUserException, NotFoundException, IsDuplicatedException, TextTooShortException {

        if (creatorEmail.equals(targetEmail)) throw new IsSameUserException();
        if (name == null || "".equals(name)) throw new TextTooShortException();
        var creatorUser = userService.findByEmail(creatorEmail);
        var targetUser = userService.findByEmail(targetEmail);
        if (targetUser.isEmpty() || creatorUser.isEmpty()) throw new NotFoundException();

        var creatorWallet = creatorUser.get().getWallet();
        var targetWallet = targetUser.get().getWallet();

        var existingConnection = find(creatorWallet, targetWallet);
        if (existingConnection.isPresent()) throw new IsDuplicatedException();

        var connection = new Connection();
        connection.setCreator(creatorWallet);
        connection.setTarget(targetWallet);
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

    public void saveTransfer(Connection connection, String description, int amountInCents) {
        transferService.save(connection, description, amountInCents);
    }

    public Map<Long, String> getAvailableConnections(String activeUserEmail) {
        var connections = connectionRepository.findByCreator_user_email(activeUserEmail);
        return connections.stream().collect(Collectors.toMap(
                                Connection::getId,
                                Connection::getName));
    }

}
