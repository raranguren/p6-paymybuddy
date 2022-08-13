package com.ricaragas.paymybuddy.unit;

import com.ricaragas.paymybuddy.model.User;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.WalletRepository;
import com.ricaragas.paymybuddy.service.UserService;
import com.ricaragas.paymybuddy.service.WalletService;
import com.ricaragas.paymybuddy.service.exceptions.IsCurrentUser;
import com.ricaragas.paymybuddy.service.exceptions.IsDuplicated;
import com.ricaragas.paymybuddy.service.exceptions.NotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @InjectMocks
    WalletService walletService;

    @Mock
    WalletRepository walletRepository;
    Wallet walletA;
    Wallet walletB;

    @Mock
    UserService userService;
    User userA;
    User userB;

    @BeforeEach
    public void before_each() {
        walletA = new Wallet();
        walletB = new Wallet();
        userA = new User();
        userB = new User();
        walletA.setUser(userA);
        walletB.setUser(userB);
        userA.setWallet(walletA);
        userB.setWallet(walletB);
        userA.setEmail("a@mail.com");
        userB.setEmail("b@mail.com");
        when(userService.getAuthenticatedUser()).thenReturn(Optional.of(userA));
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
        assertEquals(wallet, result);
        verifyNoInteractions(walletRepository);
    }

    @Test void when_add_connection_then_success() throws Exception {
        // ARRANGE
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(userB));
        // ACT
        walletService.addConnection(userB.getEmail());
        // ASSERT
        verify(walletRepository).save(walletA);
        assertEquals(walletB, walletA.getConnections().get(0));
    }

    @Test void when_add_connection_then_is_current_user() {
        // ARRANGE
        // ACT
        Executable action = () -> walletService.addConnection(userA.getEmail());
        // ASSERT
        assertThrows(IsCurrentUser.class, action);
    }
    @Test void when_add_connection_then_is_duplicated() throws Exception {
        // ARRANGE
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(userB));
        walletService.addConnection(userB.getEmail());
        // ACT
        Executable action = () -> walletService.addConnection(userB.getEmail());
        // ASSERT
        assertThrows(IsDuplicated.class, action);
    }
    @Test void when_add_connection_then_not_found() {
        // ARRANGE
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());
        // ACT
        Executable action = () -> walletService.addConnection(userB.getEmail());
        // ASSERT
        assertThrows(NotFound.class, action);
    }

}
