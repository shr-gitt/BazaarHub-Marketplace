package com.bazaarhub.backend.exception;

import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.resource.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDto> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found.", ex);
        return new ResponseEntity<>(
                new ResponseDto(ResponseStatus.ERROR.value, HttpStatus.NOT_FOUND.getReasonPhrase()),
                HttpStatus.NOT_FOUND
        );
    }
}
