package com.ricaragas.paymybuddy.integration;

import com.ricaragas.paymybuddy.model.Transfer;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.WalletRepository;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
            wallet.setProfileName("Me");
        walletRepository.save(wallet);
        // ACT
        var result = walletRepository.findById(wallet.getId());
        assert(result.isPresent());
        var contacts = result.get().getConnections();
        var transfers = result.get().getSentTransfers();
        // ASSERT
        assertThrows(LazyInitializationException.class, contacts::clear);
        assertThrows(LazyInitializationException.class, transfers::clear);
    }

    @Test
    @Transactional
    public void correctly_returns_list_of_connections() {
        // ARRANGE
        var wallet1 = new Wallet();
            wallet1.setProfileName("My friend");
        var wallet2 = new Wallet();
            wallet2.setProfileName("Me");
            wallet2.setConnections(List.of(wallet1));
        walletRepository.saveAll(List.of(wallet1,wallet2));
        // ACT
        var result = walletRepository.findById(wallet2.getId());
        assert(result.isPresent());
        var contacts = result.get().getConnections();
        // ASSERT
        assertEquals("My friend", contacts.get(0).getProfileName());
    }

    @Test
    @Transactional
    public void correctly_returns_list_of_transfers_sent() {
        // ARRANGE
        var wallet1 = new Wallet();
            wallet1.setProfileName("My friend");
        var wallet2 = new Wallet();
            wallet2.setProfileName("Me");
        var transfer = new Transfer();
            transfer.setSender(wallet2);
            transfer.setReceiver(wallet1);
            transfer.setAmountInCents(2000);
        walletRepository.saveAll(List.of(wallet1,wallet2));
        // ACT
        var result = walletRepository.findById(wallet2.getId());
        assert(result.isPresent());
        var transfers = result.get().getSentTransfers();
        // ASSERT
        assertEquals(2000, transfers.get(0).getAmountInCents());
        assertEquals("My friend", transfers.get(0).getReceiver().getProfileName());
        assertEquals("Me", transfers.get(0).getSender().getProfileName());
    }

}
