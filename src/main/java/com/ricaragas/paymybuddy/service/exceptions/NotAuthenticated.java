package com.ricaragas.paymybuddy.service.exceptions;

import org.springframework.security.access.AccessDeniedException;

public class NotAuthenticated extends AccessDeniedException {

    public NotAuthenticated() {
        super("Not Authenticated");
    }
}
