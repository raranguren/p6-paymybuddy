package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.exceptions.NotAuthenticatedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class PrincipalService {

    // Separate call to SecurityContextHolder to allow easier unit tests

    public String getEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new NotAuthenticatedException();
        }
        var principal = (UserDetails) authentication.getPrincipal();
        return principal.getUsername();
    }
}
