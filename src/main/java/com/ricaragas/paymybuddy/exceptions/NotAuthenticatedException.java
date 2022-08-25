package com.ricaragas.paymybuddy.exceptions;

import org.springframework.security.access.AccessDeniedException;

public class NotAuthenticatedException extends AccessDeniedException {

    public NotAuthenticatedException() {
        super("Not Authenticated");
    }
}
