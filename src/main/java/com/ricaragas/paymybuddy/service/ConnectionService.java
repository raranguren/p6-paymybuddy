package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.exceptions.IsDuplicatedException;
import com.ricaragas.paymybuddy.exceptions.NotFoundException;
import com.ricaragas.paymybuddy.model.Connection;
import com.ricaragas.paymybuddy.repository.ConnectionRepository;
import com.ricaragas.paymybuddy.exceptions.IsSameUserException;
import com.ricaragas.paymybuddy.exceptions.TextTooShortException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final WalletService walletService;
    ConnectionService(ConnectionRepository connectionRepository, WalletService walletService) {
        this.connectionRepository = connectionRepository;
        this.walletService = walletService;
    }

    public void createConnection(String targetEmail, String name)
            throws IsSameUserException, NotFoundException, IsDuplicatedException, TextTooShortException {

        var creatorWallet = walletService.getActiveWallet();
        var creatorEmail = creatorWallet.getUser().getEmail();

        if (creatorEmail.equals(targetEmail)) throw new IsSameUserException();
        if (name == null || "".equals(name)) throw new TextTooShortException();
        if (connectionRepository.findByCreatorAndTarget_user_email(creatorWallet, targetEmail)
                .isPresent()) throw new IsDuplicatedException();

        var targetWallet = walletService.findByEmail(targetEmail)
                .orElseThrow(NotFoundException::new);

        var connection = new Connection();
        connection.setCreator(creatorWallet);
        connection.setTarget(targetWallet);
        connection.setName(name);
        connectionRepository.save(connection);
    }

    public Optional<Connection> findById(Long receiverConnectionId) {
        return walletService.getActiveWallet().getConnections().stream()
                .filter(connection -> connection.getId().equals(receiverConnectionId))
                .findFirst();
    }

    public Map<Long, String> getAvailableConnections() {
        return walletService.getActiveWallet().
                getConnections().stream().collect(Collectors.toMap(
                                Connection::getId,
                                Connection::getName));
    }

}
