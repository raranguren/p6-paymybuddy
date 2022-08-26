package com.ricaragas.paymybuddy.unit;

import com.ricaragas.paymybuddy.model.User;
import com.ricaragas.paymybuddy.repository.UserRepository;
import com.ricaragas.paymybuddy.service.UserService;
import com.ricaragas.paymybuddy.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    WalletService walletService;

    @Test
    void when_load_principal_then_uses_email_as_username() {
        // ARRANGE
        var email = "name@example.com";
        var response = Optional.of(new User());
        when(userRepository.findByEmail(anyString())).thenReturn(response);
        // ACT
        userService.loadUserByUsername(email);
        // ASSERT
        verify(userRepository).findByEmail(email);
    }

    @Test
    void when_load_principal_then_fails() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        // ACT
        Executable action = () -> userService.loadUserByUsername(anyString());
        // ASSERT
        assertThrows(UsernameNotFoundException.class, action);
    }

    @Test
    void when_create_user_then_success() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        // ACT
        userService.createUser("new@email.com", "validPass.123");
        // ASSERT
        verify(userRepository).save(any());
        verify(walletService).createWallet(any());
    }

    @Test
    void when_create_user_then_invalid_email() {
        // ARRANGE
        // ACT
        Executable action = () -> userService.createUser("xxx", "validPass.123");
        // ASSERT
        assertThrows(IllegalArgumentException.class, action);
    }

    @Test
    void when_create_user_then_duplicated_email() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        // ACT
        Executable action = () -> userService.createUser("new@email.com", "validPass.123");
        // ASSERT
        assertThrows(IllegalArgumentException.class, action);
    }


}
