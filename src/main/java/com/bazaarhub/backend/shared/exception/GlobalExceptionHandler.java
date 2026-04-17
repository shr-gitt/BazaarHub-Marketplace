package com.bazaarhub.backend.shared.exception;

import com.bazaarhub.backend.feature.customerProfile.exception.CustomerProfileNotFoundException;
import com.bazaarhub.backend.feature.category.exception.CategoryAlreadyExistsException;
import com.bazaarhub.backend.feature.category.exception.CategoryNotFoundException;
import com.bazaarhub.backend.feature.product.exception.InvalidDiscountPriceException;
import com.bazaarhub.backend.feature.product.exception.InvalidPriceException;
import com.bazaarhub.backend.feature.product.exception.ProductNotFoundException;
import com.bazaarhub.backend.feature.user.exception.EmailAlreadyExistsException;
import com.bazaarhub.backend.feature.user.exception.PhoneNumberAlreadyExistsException;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.vendor.exception.VendorInactiveException;
import com.bazaarhub.backend.feature.vendor.exception.VendorNotFoundException;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.PlaceholderResolutionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiResponseDto<?>> buildErrorResponse(String message, HttpStatus status) {
        ApiResponseDto<?> response = new ApiResponseDto<>(ResponseStatus.ERROR.value, message, status);
        return ResponseEntity.status(status).body(response);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<?>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDto<?>> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("One or more fields have an incorrect data type. Please check your input.", ex);
        return buildErrorResponse("One or more fields have an incorrect data type. Please check your input.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(VendorInactiveException.class)
    public ResponseEntity<ApiResponseDto<?>> handleVendorInactiveException(VendorInactiveException ex) {
        log.error("Vendor not found. ", ex);
        return buildErrorResponse("Vendor is not active.", HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(VendorNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> handleVendorNotFoundException(VendorNotFoundException ex) {
        log.error("Vendor not found.", ex);
        return buildErrorResponse("Vendor not found.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto<?>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity violation", ex);
        return buildErrorResponse("Database constraint violation", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseDto<?>> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Constraint violation", ex);
        return buildErrorResponse("Constraint violation: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ApiResponseDto<?>> handleSQLIntegrityViolation(SQLIntegrityConstraintViolationException ex) {
        log.error("SQL integrity constraint violation", ex);
        return buildErrorResponse("Database constraint violated: " + ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found.", ex);
        return buildErrorResponse("Resource not found.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<?>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error("Method argument not valid", ex);
        return buildErrorResponse("Invalid data", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> handleUserNotFound(UserNotFoundException ex) {
        log.error("User not found.", ex);
        return buildErrorResponse("User not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDto<?>> handleEmailAlreadyExist(EmailAlreadyExistsException ex) {
        log.error("Email already exists", ex);
        return buildErrorResponse("Email already exists.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PhoneNumberAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDto<?>> handlePhoneNumberAlreadyExist(PhoneNumberAlreadyExistsException ex) {
        log.error("Phone number already exists", ex);
        return buildErrorResponse("Phone number already exists.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> handleNoResourceNotFound(NoResourceFoundException ex) {
        log.error("Resource not found.", ex);
        return buildErrorResponse("Resource not found.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomerProfileNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> customerProfileNotFoundException(CustomerProfileNotFoundException ex) {
        log.error("CustomerProfileNotFoundException.", ex);
        return buildErrorResponse("CustomerProfileNotFoundException.", HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponseDto<?>> numberFormatException(NumberFormatException ex) {
        log.error("Invalid number format provided..", ex);
        return buildErrorResponse("Invalid number format provided..", HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDto<?>> handleCategoryAlreadyExists(CategoryAlreadyExistsException ex) {
        log.error("Category already exist", ex);
        return buildErrorResponse("Category already exists.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> handleCategoryNotFound(CategoryNotFoundException ex) {
        log.error("Category not found", ex);
        return buildErrorResponse("Category not found.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PlaceholderResolutionException.class)
    public ResponseEntity<ApiResponseDto<?>> placeholderResolutionException(CategoryNotFoundException ex) {
        log.error("Some information is missing or unavailable. Please contact support if the issue continues.", ex);
        return buildErrorResponse("Some information is missing or unavailable. Please contact support if the issue continues.", HttpStatus.NOT_FOUND);
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> handleProductNotFoundException(ProductNotFoundException ex) {
        log.error("Product not found.", ex);
        return buildErrorResponse("Product not found.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPriceException.class)
    public ResponseEntity<ApiResponseDto<?>> handleInvalidPriceException(InvalidPriceException ex) {
        log.error("Invalid price.", ex);
        return buildErrorResponse("Invalid Price", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDiscountPriceException.class)
    public ResponseEntity<ApiResponseDto<?>> handleInvalidPriceException(InvalidDiscountPriceException ex) {
        log.error("Invalid discount price.", ex);
        return buildErrorResponse("Invalid discount Price", HttpStatus.BAD_REQUEST);
    }


}
