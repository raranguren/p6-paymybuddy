package com.ricaragas.paymybuddy.unit;

import com.ricaragas.paymybuddy.model.Transfer;
import com.ricaragas.paymybuddy.model.User;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.WalletRepository;
import com.ricaragas.paymybuddy.service.TransferService;
import com.ricaragas.paymybuddy.service.UserService;
import com.ricaragas.paymybuddy.service.WalletService;
import com.ricaragas.paymybuddy.service.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    TransferService transferService;

    @BeforeEach
    public void before_each() {
        walletA = new Wallet();
        walletB = new Wallet();
        walletA.setId(1L);
        walletB.setId(2L);
        userA = new User();
        userB = new User();
        walletA.setProfileName("me");
        walletB.setProfileName("my friend");
        walletA.setUser(userA);
        walletB.setUser(userB);
        userA.setWallet(walletA);
        userB.setWallet(walletB);
        userA.setEmail("a@mail.com");
        userB.setEmail("b@mail.com");
        when(userService.getAuthenticatedUser()).thenReturn(Optional.of(userA));
    }

    @Test
    public void when_add_connection_then_success() throws Exception {
        // ARRANGE
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(userB));
        // ACT
        walletService.addConnection(userB.getEmail());
        // ASSERT
        verify(walletRepository).save(walletA);
        assertEquals(walletB, walletA.getConnections().get(0));
    }

    @Test
    public void when_add_connection_then_is_current_user() {
        // ARRANGE
        // ACT
        Executable action = () -> walletService.addConnection(userA.getEmail());
        // ASSERT
        assertThrows(IsCurrentUser.class, action);
    }

    @Test
    public void when_add_connection_then_is_duplicated() throws Exception {
        // ARRANGE
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(userB));
        walletService.addConnection(userB.getEmail());
        // ACT
        Executable action = () -> walletService.addConnection(userB.getEmail());
        // ASSERT
        assertThrows(IsDuplicated.class, action);
    }

    @Test
    public void when_add_connection_then_not_found() {
        // ARRANGE
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());
        // ACT
        Executable action = () -> walletService.addConnection(userB.getEmail());
        // ASSERT
        assertThrows(NotFound.class, action);
    }

    @Test
    public void when_pay_then_success() throws Exception {
        // ARRANGE
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(userB));
        walletService.addConnection(userB.getEmail());
        verify(walletRepository, times(1)).save(walletA);
        walletA.setBalanceInCents(1010);
        walletB.setBalanceInCents(1000);
        // ACT
        walletService.pay(walletB.getId(), "description", 10.1);
        // ASSERT
        verify(transferService, times(1))
                .save(walletA, walletB, "description", 1010);
        verify(walletRepository, times(2)).save(walletA);
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
        assertThrows(TextTooShort.class, action);
    }

    @Test
    public void when_pay_negative_then_invalid_amount() {
        // ARRANGE
        // ACT
        Executable action = () -> walletService.pay(walletB.getId(), "description", -1);
        // ASSERT
        assertThrows(InvalidAmount.class, action);
    }

    @Test
    public void when_pay_then_connection_not_enough_balance() {
        // ARRANGE
        walletA.setBalanceInCents(100);
        // ACT
        Executable action = () -> walletService.pay(123L, "description", 100);
        // ASSERT
        assertThrows(NotEnoughBalance.class, action);
    }

    @Test
    public void when_pay_then_connection_not_found() {
        // ARRANGE
        walletA.setBalanceInCents(4000);
        long idNotConnected = 123L;
        // ACT
        Executable action = () -> walletService.pay(idNotConnected, "description", 20);
        // ASSERT
        assertThrows(NotFound.class, action);
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

    @Test
    public void when_get_connections_then_success() throws Exception{
        // ARRANGE
        walletA.setConnections(List.of(walletB));
        // ACT
        var result = walletService.getConnectionOptions();
        // ASSERT
        assertEquals(walletB.getProfileName(), result.get(walletB.getId()));
        assertEquals(1, result.size());
    }

    @Test
    public void when_get_first_page_of_transfers_sent_then_success() throws Exception{
        // ARRANGE
        walletA.setConnections(List.of(walletB));
        var cents = 1111;
        var description = "test 11.11 euros";
        var transfer = new Transfer();
        transfer.setSender(walletA);
        transfer.setReceiver(walletB);
        transfer.setDescription(description);
        transfer.setAmountInCents(cents);
        walletA.setSentTransfers(List.of(transfer));
        // ACT
        var result = walletService.getSentTransfersPage(1);
        // ASSERT
        assertEquals(1, result.size());
        assertEquals(cents, result.get(0).getAmountInCents());
        assertEquals(description, result.get(0).getDescription());
        assertEquals(walletA, result.get(0).getSender());
        assertEquals(walletB, result.get(0).getReceiver());
    }
}
