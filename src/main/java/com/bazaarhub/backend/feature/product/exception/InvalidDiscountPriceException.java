package com.bazaarhub.backend.feature.product.exception;

public class InvalidDiscountPriceException extends RuntimeException {
  public InvalidDiscountPriceException(String message) {
    super(message);
  }
}
