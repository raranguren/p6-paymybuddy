package com.ricaragas.paymybuddy.unit;

import com.ricaragas.paymybuddy.model.Transfer;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.TransferRepository;
import com.ricaragas.paymybuddy.service.TransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        var description = "description";
        var amount = 1111;
        ArgumentCaptor<Transfer> argument = ArgumentCaptor.forClass(Transfer.class);
        // ACT
        transferService.save(sender, receiver, description, amount);
        // ASSERT
        verify(transferRepository, times(1)).save(any());
        verify(transferRepository).save(argument.capture());
        assertNotNull(argument.getValue().getTimeCompleted());
    }

}
