package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.model.User;
import com.ricaragas.paymybuddy.model.UserPrincipal;
import com.ricaragas.paymybuddy.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final WalletService walletService;
    UserService(UserRepository userRepository, WalletService walletService) {
        this.userRepository = userRepository;
        this.walletService = walletService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = findByEmail(username);
        if (user.isEmpty()) throw new UsernameNotFoundException(username);
        return new UserPrincipal(user.get());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void createUser(String email, String rawPassword) throws IllegalArgumentException {
        if (!isValidEmail(email)) throw new IllegalArgumentException();
        if (findByEmail(email).isPresent()) throw new IllegalArgumentException();
        if (!isValidPassword(rawPassword)) throw new IllegalArgumentException();
        var user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword(rawPassword));
        userRepository.save(user);
        walletService.createWallet(user);
    }

    private boolean isValidPassword(String rawPassword) {
        return rawPassword != null;
    }

    private String hashedPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    private boolean isValidEmail(String email) {
        // Validation using RFC 5322 standard
        var regexPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        return Pattern.compile(regexPattern)
                .matcher(email)
                .matches();
    }
}
