package com.ricaragas.paymybuddy.unit;

import com.ricaragas.paymybuddy.dto.InvoiceDTO;
import com.ricaragas.paymybuddy.exceptions.InvalidAmountException;
import com.ricaragas.paymybuddy.exceptions.NotEnoughBalanceException;
import com.ricaragas.paymybuddy.model.User;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.repository.WalletRepository;
import com.ricaragas.paymybuddy.service.BillingService;
import com.ricaragas.paymybuddy.service.PrincipalService;
import com.ricaragas.paymybuddy.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
    String emailA = "a@mail.test";

    @Mock
    PrincipalService principalService;
    @Mock
    BillingService billingService;

    @BeforeEach
    void before_each() {
        walletA = new Wallet();
        walletA.setId(1L);
    }

    @Test
    void when_getActiveWallet_then_success() {
        // ARRANGE
        when(principalService.getEmail()).thenReturn(emailA);
        when(walletRepository.findByUser_email(emailA)).thenReturn(Optional.ofNullable(walletA));
        // ACT
        var result = walletService.getActiveWallet();
        // ASSERT
        assertEquals(result, walletA);
    }

    @Test
    void when_create_new_wallet_then_success() {
        // ARRANGE
        // ACT
        walletService.createWallet(new User());
        // ASSERT
        verify(walletRepository).save(any());
    }

    @Test
    void when_get_balance_then_success() {
        // ARRANGE
        walletA.setBalanceInCents(333);
        when(principalService.getEmail()).thenReturn("a@mail.com");
        when(walletRepository.findByUser_email("a@mail.com")).thenReturn(Optional.ofNullable(walletA));
        // ACT
        var result = walletService.getBalanceInEuros();
        // ASSERT
        assertEquals(3.33, result);
    }

    @Test
    void when_add_balance_then_success() throws Exception {
        // ARRANGE
        walletA.setBalanceInCents(200);
        // ACT
        walletService.doBalanceUpdate(walletA, 100);
        // ASSERT
        assertEquals(300, walletA.getBalanceInCents());
        verify(walletRepository).save(walletA);
    }

    @Test
    void when_remove_balance_then_success() throws Exception {
        // ARRANGE
        walletA.setBalanceInCents(200);
        // ACT
        walletService.doBalanceUpdate(walletA, -100);
        // ASSERT
        assertEquals(100, walletA.getBalanceInCents());
        verify(walletRepository).save(walletA);
    }

    @Test
    void when_remove_too_much_balance_then_error() {
        // ARRANGE
        walletA.setBalanceInCents(200);
        // ACT
        Executable action = () -> walletService.doBalanceUpdate(walletA, -201);
        // ASSERT
        assertThrows(NotEnoughBalanceException.class, action);
        verify(walletRepository, times(0)).save(walletA);
    }

    @Nested
    @DisplayName("Interactions with Billing service")
    class interactions_with_billing_service {

        @Test
        void when_get_invoice_to_add_amount_then_success() throws Exception{
            // ARRANGE
            var amount = 100.0;
            var invoice = new InvoiceDTO();
            when(billingService.getInvoiceForMoneyChargeUp(anyInt(), any()))
                    .thenReturn(invoice);
            when(principalService.getEmail()).thenReturn(emailA);
            when(walletRepository.findByUser_email(emailA)).thenReturn(Optional.ofNullable(walletA));
            // ACT
            var result = walletService.getInvoiceToAddAmount(amount);
            // ASSERT
            assertEquals(result, invoice);
        }

        @Test
        void when_get_invoice_to_add_negative_amount_then_fail() {
            // ARRANGE
            var amount = -1.0;
            // ACT
            Executable action = () -> walletService.getInvoiceToAddAmount(amount);
            // ASSERT
            assertThrows(InvalidAmountException.class, action);
        }

        @Test
        void when_get_invoice_to_withdraw_all_then_success() throws Exception {
            // ARRANGE
            walletA.setBalanceInCents(1000);
            var invoice = new InvoiceDTO();
            when(billingService.getInvoiceForMoneyWithdrawal(anyInt(), any()))
                    .thenReturn(invoice);
            when(principalService.getEmail()).thenReturn(emailA);
            when(walletRepository.findByUser_email(emailA)).thenReturn(Optional.ofNullable(walletA));
            // ACT
            var result = walletService.getInvoiceToWithdrawAll();
            // ASSERT
            assertEquals(result, invoice);
        }

        @Test
        void when_get_invoice_to_withdraw_nothing_then_fail() {
            // ARRANGE
            when(principalService.getEmail()).thenReturn(emailA);
            when(walletRepository.findByUser_email(emailA)).thenReturn(Optional.ofNullable(walletA));
            // ACT
            Executable action = () -> walletService.getInvoiceToWithdrawAll();
            // ASSERT
            assertThrows(NotEnoughBalanceException.class, action);
        }

        @Test
        void when_adding_balance_wait_confirmation() {
            // ARRANGE
            var invoice = new InvoiceDTO();
            invoice.setTransferInCents(1000);
            // ACT
            walletService.getUrlAndStartAddingMoney(invoice);
            // ASSERT
            verify(walletRepository, times(0)).save(any());
        }

        @Test
        void when_removing_balance_wait_confirmation() throws Exception {
            // ARRANGE
            var amount = 1000;
            var invoice = new InvoiceDTO();
            invoice.setTransferInCents(-amount); // negative value

            walletA.setBalanceInCents(amount);
            when(principalService.getEmail()).thenReturn(emailA);
            when(walletRepository.findByUser_email(emailA)).thenReturn(Optional.ofNullable(walletA));
            // ACT
            walletService.startBalanceWithdrawal(invoice, amount);
            // ASSERT
            verify(walletRepository, times(0)).save(any());
        }

    }

}
