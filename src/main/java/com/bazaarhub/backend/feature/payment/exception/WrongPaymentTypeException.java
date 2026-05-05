package com.bazaarhub.backend.feature.payment.exception;

public class WrongPaymentTypeException extends RuntimeException {
    public WrongPaymentTypeException(String message) {
        super(message);
    }
}
