package com.bazaarhub.backend.feature.customerProfile.exception;

public class CustomerProfileNotFoundException extends RuntimeException {
    public CustomerProfileNotFoundException(String message) {
        super(message);
    }
}
