package com.ricaragas.paymybuddy.integration;

import com.ricaragas.paymybuddy.model.User;
import com.ricaragas.paymybuddy.repository.UserRepository;
import com.ricaragas.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void when_user_not_found_then_fail() {
        // ARRANGE
        // ACT
        Executable action = () -> userService.loadUserByUsername("xxx");
        // ASSERT
        assertThrows(UsernameNotFoundException.class, action);
    }

    @Test
    public void when_user_found_then_success()  {
        // ARRANGE
        String email = "test@email.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw("123",BCrypt.gensalt("$2a$")));
        userRepository.deleteAll();
        userRepository.save(user);
        // ACT
        var result = userService.loadUserByUsername(email);
        // ASSERT
        assertEquals(email, result.getUsername());
        assertTrue(result.isEnabled());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
    }

}
