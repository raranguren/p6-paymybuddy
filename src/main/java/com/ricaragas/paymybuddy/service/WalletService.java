package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.model.User;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    UserService userService;
    @Autowired
    WalletRepository walletRepository;
    public Optional<Wallet> getWalletForUser(User user) {
        return walletRepository.findByUser(user);
    }

    public Optional<Wallet> getWalletForAuthenticatedUser() {
        var user = userService.getAuthenticatedUser();
        if (user.isEmpty()) return Optional.empty();
        return Optional.of(user.get().getWallet());
    }

}
