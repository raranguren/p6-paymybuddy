package com.ricaragas.paymybuddy.unit;

import com.ricaragas.paymybuddy.model.Connection;
import com.ricaragas.paymybuddy.model.User;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.ConnectionRepository;
import com.ricaragas.paymybuddy.service.ConnectionService;
import com.ricaragas.paymybuddy.service.TransferService;
import com.ricaragas.paymybuddy.exceptions.IsSameUserException;
import com.ricaragas.paymybuddy.exceptions.TextTooShortException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConnectionServiceTest {

    @InjectMocks
    ConnectionService connectionService;

    @Mock
    ConnectionRepository connectionRepository;

    @Mock
    TransferService transferService;

    User userA;
    User userB;
    Wallet walletA;
    Wallet walletB;
    Connection connectionAB;

    @BeforeEach
    public void before_each() {
        userA = new User();
        userB = new User();
        walletA = new Wallet();
        walletB = new Wallet();
        walletA.setUser(userA);
        walletB.setUser(userB);
        connectionAB = new Connection();
        connectionAB.setCreator(walletA);
        connectionAB.setTarget(walletB);
        connectionAB.setId(123L);
    }

    @Test
    public void when_save_with_no_name_then_fail() {
        // ARRANGE
        var name = "";
        // ACT
        Executable action = () -> connectionService.save("my@email", "another@email", name);
        // ASSERT
        assertThrows(TextTooShortException.class, action);
    }

    @Test
    public void when_save_self_connection_then_fail() {
        // ARRANGE
        var name= "me";
        // ACT
        Executable action = () -> connectionService.save("my@email", "my@email", name);
        // ASSERT
        assertThrows(IsSameUserException.class, action);
    }

    @Test
    public void when_find_connection_use_repository() {
        // ARRANGE
        when(connectionRepository.findByCreatorAndTarget(walletA, walletB))
                .thenReturn(Optional.ofNullable(connectionAB));
        // ACT
        var result = connectionService.find(walletA, walletB);
        // ASSERT
        verify(connectionRepository).findByCreatorAndTarget(walletA, walletB);
        assert(result.isPresent());
    }

    @Test
    public void when_find_connection_by_id_use_wallet_given() {
        // ARRANGE
        // ACT
        var result = connectionService.findById(walletA, connectionAB.getId());
        // ASSERT
        verifyNoInteractions(connectionRepository);
        assert(result.isPresent());
    }

    @Test
    public void when_save_transfer_call_service() {
        // ARRANGE
        var description = "reason for the transfer";
        int amount = 1020;
        // ACT
        connectionService.saveTransfer(connectionAB, description, amount);
        // ASSERT
        verify(transferService).save(connectionAB, description, amount);
    }

}
