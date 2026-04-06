package com.bazaarhub.backend.exception;

import com.bazaarhub.backend.shared.exception.GlobalExceptionHandler;
import com.bazaarhub.backend.shared.exception.ResourceNotFoundException;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound() {
        ResourceNotFoundException ex =
                new ResourceNotFoundException("Not found");

        ResponseEntity<ApiResponseDto<?>> response =
                handler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("1", response.getBody().getStatusCode());
        assertEquals("Not Found", response.getBody().getMessage());
    }
}