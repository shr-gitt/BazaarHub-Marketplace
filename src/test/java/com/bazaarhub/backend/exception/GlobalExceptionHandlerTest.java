package com.bazaarhub.backend.exception;

import com.bazaarhub.backend.feature.category.exception.CategoryAlreadyExistsException;
import com.bazaarhub.backend.feature.category.exception.CategoryNotFoundException;
import com.bazaarhub.backend.feature.product.exception.InvalidDiscountPriceException;
import com.bazaarhub.backend.feature.product.exception.InvalidPriceException;
import com.bazaarhub.backend.feature.product.exception.ProductNotFoundException;
import com.bazaarhub.backend.feature.user.exception.EmailAlreadyExistsException;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.vendorProfile.exception.VendorInactiveException;
import com.bazaarhub.backend.feature.vendorProfile.exception.VendorNotFoundException;
import com.bazaarhub.backend.shared.exception.*;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_shouldReturnNotFound() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleResourceNotFound(new ResourceNotFoundException("Not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Resource not found.", response.getBody().getMessage());
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleGenericException(new Exception("Unexpected error"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().getMessage());
    }

    @Test
    void handleIllegalArgumentException_shouldReturnBadRequest() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleIllegalArgumentException(new IllegalArgumentException("Invalid"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Illegal argument", response.getBody().getMessage());
    }

    @Test
    void handleUserNotFound_shouldReturnNotFound() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleUserNotFound(new UserNotFoundException("User not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void handleEmailAlreadyExist_shouldReturnBadRequest() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleEmailAlreadyExist(new EmailAlreadyExistsException("Email exists"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Email already exists.", response.getBody().getMessage());
    }

    @Test
    void handleVendorNotFoundException_shouldReturnNotFound() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleVendorNotFoundException(new VendorNotFoundException("Vendor not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Vendor not found.", response.getBody().getMessage());
    }

    @Test
    void handleVendorInactiveException_shouldReturnNotAcceptable() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleVendorInactiveException(new VendorInactiveException("Inactive"));

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Vendor is not active.", response.getBody().getMessage());
    }

    @Test
    void handleCategoryAlreadyExists_shouldReturnBadRequest() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleCategoryAlreadyExists(new CategoryAlreadyExistsException("Exists"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Category already exists.", response.getBody().getMessage());
    }

    @Test
    void handleCategoryNotFound_shouldReturnNotFound() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleCategoryNotFound(new CategoryNotFoundException("Not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Category not found.", response.getBody().getMessage());
    }

    @Test
    void handleProductNotFoundException_shouldReturnNotFound() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleProductNotFoundException(new ProductNotFoundException("Not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product not found.", response.getBody().getMessage());
    }

    @Test
    void handleInvalidPriceException_shouldReturnBadRequest() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleInvalidPriceException(new InvalidPriceException("Invalid price"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid Price", response.getBody().getMessage());
    }

    @Test
    void handleInvalidDiscountPriceException_shouldReturnBadRequest() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleInvalidPriceException(new InvalidDiscountPriceException("Invalid discount"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid discount Price", response.getBody().getMessage());
    }

    @Test
    void handleOrderNotFoundException_shouldReturnBadRequest() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleOrderNotFoundException(new OrderNotFoundException("Order not found"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Order Not Found Exception.", response.getBody().getMessage());
    }

    @Test
    void handleInvalidOrderStateException_shouldReturnBadRequest() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleInvalidOrderStateException(new InvalidOrderStateException("Invalid state"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid Order State Not Found Exception.", response.getBody().getMessage());
    }

    @Test
    void handleEmptyCartCheckoutException_shouldReturnBadRequest() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleEmptyCartCheckoutException(new EmptyCartCheckoutException("Empty cart"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Empty Cart Checkout Exception.", response.getBody().getMessage());
    }

    @Test
    void handleInvalidCredentialException_shouldReturnBadRequest() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleInvalidCredentialException(new InvalidCredentialException("Invalid"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid Credentials.", response.getBody().getMessage());
    }

    @Test
    void handleEntityExistsException_shouldReturnBadRequest() {
        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleEntityExistsException(new EntityExistsException("Exists"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Information already exists.", response.getBody().getMessage());
    }

}