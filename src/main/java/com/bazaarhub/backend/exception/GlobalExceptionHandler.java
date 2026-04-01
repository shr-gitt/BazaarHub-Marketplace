package com.bazaarhub.backend.exception;

import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found.", ex);
        ApiResponseDto<?> response = new ApiResponseDto<>(ResponseStatus.ERROR.value, HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<?>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error("Method argument not valid", ex);
        ApiResponseDto<?> response = new ApiResponseDto<>(ResponseStatus.ERROR.value, HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> handleUserNotFound(UserNotFoundException ex) {
        log.error("User not found.", ex);
        ApiResponseDto<?> response = new ApiResponseDto<>(ResponseStatus.ERROR.value, HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(response);
    }
}
