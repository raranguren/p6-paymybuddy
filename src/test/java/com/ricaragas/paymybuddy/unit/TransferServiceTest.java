package com.ricaragas.paymybuddy.unit;

import com.ricaragas.paymybuddy.model.Connection;
import com.ricaragas.paymybuddy.model.Transfer;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.TransferRepository;
import com.ricaragas.paymybuddy.service.TransferService;
import com.ricaragas.paymybuddy.service.dto.TransferRowDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @InjectMocks
    TransferService transferService;

    @Mock
    TransferRepository transferRepository;

    @Test
    public void when_save_then_success_with_timestamp() {
        // ARRANGE
        var sender = new Wallet();
        var receiver = new Wallet();
        var connection = new Connection();
        connection.setCreator(sender);
        connection.setTarget(receiver);
        var description = "description";
        var amount = 1111;
        ArgumentCaptor<Transfer> argument = ArgumentCaptor.forClass(Transfer.class);
        // ACT
        transferService.save(new Connection(), description, amount);
        // ASSERT
        verify(transferRepository, times(1)).save(any());
        verify(transferRepository).save(argument.capture());
        assertNotNull(argument.getValue().getTimeCompleted());
    }

    @Test
    public void when_get_transfer_rows_then_success() {
        // ARRANGE
        var name = "name";
        var description = "description";
        var amount = 123.0;
        var connection = new Connection();
        connection.setName(name);
        var transfer = new Transfer();
        transfer.setConnection(connection);
        transfer.setAmountInCents(12300);
        transfer.setDescription(description);
        // ACT
        var results = transferService.getTransferRows(connection);
        TransferRowDTO result = results.get(0);
        // ASSERT
        assertEquals(1, results.size());
        assertEquals(name, result.name);
        assertEquals(description, result.description);
        assertEquals(amount, result.euros);
    }

}
