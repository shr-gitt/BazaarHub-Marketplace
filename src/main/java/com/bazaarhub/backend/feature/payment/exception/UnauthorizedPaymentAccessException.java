package com.bazaarhub.backend.feature.payment.exception;

public class UnauthorizedPaymentAccessException extends RuntimeException {
    public UnauthorizedPaymentAccessException(String message) {
        super(message);
    }
}
