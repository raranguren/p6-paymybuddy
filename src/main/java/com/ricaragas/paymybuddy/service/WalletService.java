package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.WalletRepository;
import com.ricaragas.paymybuddy.service.exceptions.IsCurrentUser;
import com.ricaragas.paymybuddy.service.exceptions.IsDuplicated;
import com.ricaragas.paymybuddy.service.exceptions.NotAuthenticated;
import com.ricaragas.paymybuddy.service.exceptions.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.hibernate.Hibernate.initialize;

@Service
public class WalletService {

    @Autowired
    UserService userService;
    @Autowired
    WalletRepository walletRepository;

    @Transactional
    public Wallet getWalletForAuthenticatedUser() {
        var wallet = nonTransactionalGetWalletForAuthenticatedUser();
        initialize(wallet.getConnections());
        initialize(wallet.getSentTransfers());
        return wallet;
    }

    private Wallet nonTransactionalGetWalletForAuthenticatedUser() {
        var user = userService.getAuthenticatedUser();
        if (user.isEmpty()) throw new NotAuthenticated();
        return user.get().getWallet();
    }

    public void addConnection(String email) throws IsCurrentUser, NotFound, IsDuplicated {
        var currentWallet = nonTransactionalGetWalletForAuthenticatedUser();
        if (email.equals(currentWallet.getUser().getEmail()))
            throw new IsCurrentUser();
        var userToAdd = userService.findByEmail(email);
        if (userToAdd.isEmpty()) throw new NotFound();
        var connections = currentWallet.getConnections();
        var newConnection = userToAdd.get().getWallet();
        if (connections.contains(newConnection)) throw new IsDuplicated();
        connections.add(newConnection);
        walletRepository.save(currentWallet);
    }

}
