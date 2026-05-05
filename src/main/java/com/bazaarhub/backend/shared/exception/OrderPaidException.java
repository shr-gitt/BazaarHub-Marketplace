package com.bazaarhub.backend.shared.exception;

public class OrderPaidException extends RuntimeException {
    public OrderPaidException(String message) {
        super(message);
    }
}
