package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.model.User;
import com.ricaragas.paymybuddy.model.UserPrincipal;
import com.ricaragas.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = findByEmail(username);
        if (user.isEmpty()) throw new UsernameNotFoundException(username);
        return new UserPrincipal(user.get());
    }

    public Optional<User> getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) return Optional.empty();
        var loggedUserPrincipal = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(loggedUserPrincipal.getUsername());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
