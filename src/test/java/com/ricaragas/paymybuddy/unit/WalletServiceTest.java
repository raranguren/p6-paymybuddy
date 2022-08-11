package com.ricaragas.paymybuddy.unit;

import com.ricaragas.paymybuddy.model.User;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.WalletRepository;
import com.ricaragas.paymybuddy.service.UserService;
import com.ricaragas.paymybuddy.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @InjectMocks
    WalletService walletService;

    @Mock
    WalletRepository walletRepository;

    @Mock
    UserService userService;

    @Test
    public void when_get_wallet_by_user_then_success() {
        // ARRANGE
        var user = new User();
        var wallet = new Wallet();
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        // ACT
        var result = walletService.getWalletForUser(user);
        // ASSERT
        assert(result.isPresent());
        assertEquals(wallet, result.get());
    }

    @Test
    public void when_get_wallet_by_user_then_empty() {
        // ARRANGE
        var user = new User();
        when(walletRepository.findByUser(user)).thenReturn(Optional.empty());
        // ACT
        var result = walletService.getWalletForUser(user);
        // ASSERT
        assert(result.isEmpty());
    }

    @Test
    public void when_get_wallet_for_logged_user_then_success() {
        // ARRANGE
        var user = new User();
        var wallet = new Wallet();
        user.setWallet(wallet);
        when(userService.getAuthenticatedUser()).thenReturn(Optional.of(user));
        // ACT
        var result = walletService.getWalletForAuthenticatedUser();
        // ASSERT
        assert(result.isPresent());
        assertEquals(wallet, result.get());
        verifyNoInteractions(walletRepository);
    }

}
