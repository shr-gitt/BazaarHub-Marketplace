package com.bazaarhub.backend.shared.resource;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
public final class ResponseDto<T> {
    private final String statusCode;
    private final String message;
    @Nullable
    private final T data;

    public ResponseDto(String statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public ResponseDto(String statusCode, String message) {
        this(statusCode, message, null);
    }
}
