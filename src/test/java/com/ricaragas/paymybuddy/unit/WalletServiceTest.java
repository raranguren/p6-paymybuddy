package com.ricaragas.paymybuddy.unit;

import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.WalletRepository;
import com.ricaragas.paymybuddy.service.PrincipalService;
import com.ricaragas.paymybuddy.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
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
    Wallet walletA;
    Wallet walletB;

    @Mock
    PrincipalService principalService;

    @BeforeEach
    public void before_each() {
        walletA = new Wallet();
        walletB = new Wallet();
        walletA.setId(1L);
        walletB.setId(2L);
    }

    @Test
    public void when_get_balance_then_success() {
        // ARRANGE
        walletA.setBalanceInCents(333);
        when(principalService.getEmail()).thenReturn("a@mail.com");
        when(walletRepository.findByUser_email("a@mail.com")).thenReturn(Optional.ofNullable(walletA));
        // ACT
        var result = walletService.getBalanceInEuros();
        // ASSERT
        assertEquals(3.33, result);
    }

}
