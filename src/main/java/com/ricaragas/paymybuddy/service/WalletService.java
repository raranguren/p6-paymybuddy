package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.model.User;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hibernate.Hibernate.initialize;

@Service
public class WalletService {

    @Autowired
    UserService userService;
    @Autowired
    WalletRepository walletRepository;
    public Optional<Wallet> getWalletForUser(User user) {
        return walletRepository.findByUser(user);
    }

    @Transactional
    public Optional<Wallet> getWalletForAuthenticatedUser() {
        var user = userService.getAuthenticatedUser();
        if (user.isEmpty()) return Optional.empty();
        var wallet = user.get().getWallet();
        initialize(wallet.getConnections());
        initialize(wallet.getSentTransfers());
        return Optional.of(wallet);
    }

}
