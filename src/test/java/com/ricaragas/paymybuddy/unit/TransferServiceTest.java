package com.ricaragas.paymybuddy.unit;

import com.ricaragas.paymybuddy.exceptions.InvalidAmountException;
import com.ricaragas.paymybuddy.exceptions.NotEnoughBalanceException;
import com.ricaragas.paymybuddy.exceptions.NotFoundException;
import com.ricaragas.paymybuddy.exceptions.TextTooShortException;
import com.ricaragas.paymybuddy.model.Connection;
import com.ricaragas.paymybuddy.model.Transfer;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.TransferRepository;
import com.ricaragas.paymybuddy.service.ConnectionService;
import com.ricaragas.paymybuddy.service.TransferService;
import com.ricaragas.paymybuddy.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @InjectMocks
    TransferService transferService;
    @Mock
    TransferRepository transferRepository;
    @Mock
    WalletService walletService;
    Wallet walletA, walletB;
    @Mock
    ConnectionService connectionService;
    Connection connectionAB;

    @BeforeEach
    void before_each() {
        walletA = new Wallet();
        walletB = new Wallet();
        connectionAB = new Connection();
        connectionAB.setId(123L);
        connectionAB.setName("is B");
        connectionAB.setCreator(walletA);
        connectionAB.setTarget(walletB);
    }

    @Test
    void when_list_all_transfers_then_success() {
        // ARRANGE
        var transfer = new Transfer();
        transfer.setConnection(connectionAB);
        transfer.setDescription("description test pay");
        transfer.setAmountInCents(1234);
        when(walletService.getActiveWallet()).thenReturn(walletA);
        when(transferRepository.findAllByConnection_creatorOrderByTimeCompletedDesc(any()))
                .thenReturn(connectionAB.getTransfers());
        // ACT
        var result = transferService.getSentTransfers();
        // ASSERT
        assertEquals(1, result.size());
        assertEquals("is B", result.get(0).name);
        assertEquals("description test pay", result.get(0).description);
        assertEquals(12.34, result.get(0).euros);
    }

    @Test
    void when_create_new_transfer_then_change_balances_and_success() throws Exception {
        // ARRANGE
        var description = "test gift money";
        var amount = 12.34;
        when(connectionService.findById(any())).thenReturn(Optional.ofNullable(connectionAB));
        // ACT
        transferService.createTransfer(connectionAB.getId(), description, amount);
        // ASSERT
        verify(transferRepository).save(any());
        verify(walletService, times(2)).doBalanceUpdate(any(), anyInt());
    }

    @Test
    void when_create_new_transfer_then_no_description_error() {
        // ARRANGE
        var description = "";
        var amount = 12.34;
        // ACT
        Executable action = () -> transferService.createTransfer(connectionAB.getId(), description, amount);
        // ASSERT
        assertThrows(TextTooShortException.class, action);
        verify(transferRepository, times(0)).save(any());
    }

    @Test
    void when_create_new_transfer_then_not_enough_balance_error() throws Exception {
        // ARRANGE
        var description = "test gift money";
        var amount = 12.34;
        when(connectionService.findById(any())).thenReturn(Optional.ofNullable(connectionAB));
        doThrow(NotEnoughBalanceException.class).when(walletService).doBalanceUpdate(walletA, -1234);
        // ACT
        Executable action = () -> transferService.createTransfer(connectionAB.getId(), description, amount);
        // ASSERT
        assertThrows(NotEnoughBalanceException.class, action);
        verify(transferRepository, times(0)).save(any());
    }

    @Test
    void when_attacked_with_negative_amount_then_error() {
        // ARRANGE
        var connectionId = 123L;
        var description = "test gift money";
        var amount = -10.0;
        // ACT
        Executable action = () -> transferService.createTransfer(connectionId, description, amount);
        // ASSERT
        assertThrows(InvalidAmountException.class, action);
        verify(transferRepository, times(0)).save(any());
    }

    @Test
    void when_attacked_with_wrong_connection_id_then_error() {
        // ARRANGE
        var connectionId = 666L;
        var description = "test gift money";
        var amount = 12.34;
        // ACT
        Executable action = () -> transferService.createTransfer(connectionId, description, amount);
        // ASSERT
        assertThrows(NotFoundException.class, action);
        verify(transferRepository, times(0)).save(any());
    }

}
