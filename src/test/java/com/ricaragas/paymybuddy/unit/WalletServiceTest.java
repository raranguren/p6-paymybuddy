package com.ricaragas.paymybuddy.unit;

import com.ricaragas.paymybuddy.exceptions.*;
import com.ricaragas.paymybuddy.model.Connection;
import com.ricaragas.paymybuddy.model.User;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.WalletRepository;
import com.ricaragas.paymybuddy.service.ConnectionService;
import com.ricaragas.paymybuddy.service.UserService;
import com.ricaragas.paymybuddy.service.WalletService;
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

    @Mock
    ConnectionService connectionService;

    @BeforeEach
    public void before_each() {
        walletA = new Wallet();
        walletB = new Wallet();
        walletA.setId(1L);
        walletB.setId(2L);
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
    public void when_pay_then_success() throws Exception {
        // ARRANGE
        var connection = new Connection();
        var id = 12L;
        connection.setCreator(walletA);
        connection.setTarget(walletB);
        connection.setId(id);
        when(connectionService.findById(walletA, id)).thenReturn(Optional.of(connection));
        walletA.setBalanceInCents(1010);
        walletB.setBalanceInCents(1000);
        // ACT
        walletService.pay(id, "description", 10.1);
        // ASSERT
        verify(connectionService).saveTransfer(connection, "description", 1010);
        verify(walletRepository, times(1)).save(walletA);
        verify(walletRepository, times(1)).save(walletB);
        assertEquals(0, walletA.getBalanceInCents());
        assertEquals(2010, walletB.getBalanceInCents());
    }

    @Test
    public void when_pay_then_description_too_short() {
        // ARRANGE
        // ACT
        Executable action = () -> walletService.pay(walletB.getId(), "", 1);
        // ASSERT
        assertThrows(TextTooShortException.class, action);
    }

    @Test
    public void when_pay_negative_then_invalid_amount() {
        // ARRANGE
        // ACT
        Executable action = () -> walletService.pay(walletB.getId(), "description", -1);
        // ASSERT
        assertThrows(InvalidAmountException.class, action);
    }

    @Test
    public void when_pay_then_connection_not_enough_balance() {
        // ARRANGE
        walletA.setBalanceInCents(100);
        // ACT
        Executable action = () -> walletService.pay(123L, "description", 100);
        // ASSERT
        assertThrows(NotEnoughBalanceException.class, action);
    }

    @Test
    public void when_pay_then_connection_not_found() {
        // ARRANGE
        walletA.setBalanceInCents(4000);
        long idNotConnected = 123L;
        // ACT
        Executable action = () -> walletService.pay(idNotConnected, "description", 20);
        // ASSERT
        assertThrows(NotFoundException.class, action);
    }

    @Test
    public void when_get_balance_then_success() {
        // ARRANGE
        walletA.setBalanceInCents(333);
        // ACT
        var result = walletService.getBalanceInEuros();
        // ASSERT
        assertEquals(3.33, result);
    }

}
