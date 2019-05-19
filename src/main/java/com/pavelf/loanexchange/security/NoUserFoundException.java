package com.pavelf.loanexchange.security;

import org.springframework.security.core.AuthenticationException;

public class NoUserFoundException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public NoUserFoundException(String message) {
        super(message);
    }

    public NoUserFoundException() {
        super("Could not find user.");
    }

}

