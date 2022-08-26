package com.ricaragas.paymybuddy.unit;

import com.ricaragas.paymybuddy.exceptions.IsSameUserException;
import com.ricaragas.paymybuddy.exceptions.NotFoundException;
import com.ricaragas.paymybuddy.exceptions.TextTooShortException;
import com.ricaragas.paymybuddy.model.Connection;
import com.ricaragas.paymybuddy.model.User;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.ConnectionRepository;
import com.ricaragas.paymybuddy.service.ConnectionService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConnectionServiceTest {

    @InjectMocks
    ConnectionService connectionService;
    @Mock
    ConnectionRepository connectionRepository;
    @Mock
    WalletService walletService;

    String emailA = "my@email.com";
    String emailB = "friend@email.com";
    Wallet walletA, walletB;
    User userA, userB;

    @BeforeEach
    void before_each() {
        userA = new User();
        userB = new User();
        userA.setEmail(emailA);
        userB.setEmail(emailB);
        walletA = new Wallet();
        walletB = new Wallet();
        walletA.setUser(userA);
        walletB.setUser(userB);
        when(walletService.getActiveWallet()).thenReturn(walletA);
    }

    @Test
    void when_create_connection_then_success() throws Exception {
        // ARRANGE
        when(walletService.findByEmail(emailB)).thenReturn(Optional.ofNullable(walletB));
        // ACT
        connectionService.createConnection(emailB, "my friend B");
        // ASSERT
        verify(connectionRepository).save(any());
    }

    @Test
    void when_create_connection_with_self_then_error() {
        // ARRANGE
        // ACT
        Executable action = () -> connectionService.createConnection(emailA, "me");
        // ASSERT
        assertThrows(IsSameUserException.class, action);
    }

    @Test
    void when_create_connection_without_name_then_error() {
        // ARRANGE
        // ACT
        Executable action = () -> connectionService.createConnection(emailB, "");
        // ASSERT
        assertThrows(TextTooShortException.class, action);
    }

    @Test
    void when_create_connection_does_not_exist_then_error() {
        // ARRANGE
        when(walletService.findByEmail(emailB)).thenReturn(Optional.empty());
        // ACT
        Executable action = () -> connectionService.createConnection(emailB, "some name");
        // ASSERT
        assertThrows(NotFoundException.class, action);
    }

    @Test
    void when_findById_then_success() {
        // ARRANGE
        var connectionAB = new Connection();
        connectionAB.setCreator(walletA);
        connectionAB.setId(11L);
        connectionAB.setName("friend B");
        // ACT
        var result = connectionService.findById(11L);
        // ASSERT
        assert(result.isPresent());
        assert(result.get().getName().equals("friend B"));
    }

    @Test
    void when_findById_then_empty() {
        // ARRANGE
        // ACT
        var result = connectionService.findById(1L);
        // ASSERT
        assert(result.isEmpty());
    }

    @Test
    void when_list_connections_for_selector_then_success() {
        // ARRANGE
        var connectionAB = new Connection();
        connectionAB.setCreator(walletA);
        connectionAB.setId(12L);
        connectionAB.setName("friend B");
        // ACT
        var result = connectionService.getAvailableConnections();
        // ASSERT
        assertEquals(1, result.size());
        assertEquals("friend B", result.get(12L));
    }

}
