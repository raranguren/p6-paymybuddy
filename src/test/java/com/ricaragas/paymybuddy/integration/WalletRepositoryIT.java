package com.ricaragas.paymybuddy.integration;

import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.WalletRepository;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class WalletRepositoryIT {

    @Autowired
    WalletRepository walletRepository;

    @Test // not @Transactional
    public void model_should_make_use_of_lazy_loading() {
        // ARRANGE
        var wallet = new Wallet();
        walletRepository.save(wallet);
        // ACT
        var result = walletRepository.findById(wallet.getId());
        assert(result.isPresent());
        var connections = result.get().getConnections();
        // ASSERT
        assertThrows(LazyInitializationException.class, connections::clear);
    }

}
