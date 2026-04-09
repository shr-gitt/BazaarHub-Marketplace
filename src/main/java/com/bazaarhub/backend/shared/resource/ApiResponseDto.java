package com.bazaarhub.backend.shared.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Getter
@Setter
public final class ApiResponseDto<T> {
    private final String statusCode;
    private final String message;
    @Nullable
    private final T data;

    public ApiResponseDto(String statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public ApiResponseDto(String statusCode, String message) {
        this(statusCode, message, null);
    }
}
