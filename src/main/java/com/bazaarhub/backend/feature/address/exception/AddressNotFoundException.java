package com.bazaarhub.backend.feature.address.exception;

public class AddressNotFoundException extends RuntimeException {

    public AddressNotFoundException(Long id) {
        super("Address not found with id: " + id);
    }

    public AddressNotFoundException(String message) {
        super(message);
    }
}