package com.bazaarhub.backend.shared.exception;

public class InvalidCredentialException extends RuntimeException {
    public InvalidCredentialException(String message) {
        super(message);
    }
}
