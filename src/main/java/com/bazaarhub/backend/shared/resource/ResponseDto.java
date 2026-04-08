package com.bazaarhub.backend.shared.resource;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityListeners;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@RequiredArgsConstructor
@Getter
@Setter
public final class ResponseDto<T> {
    private final String statusCode;
    private final String message;
    @Nullable
    private T data = null;

    @CreatedDate
    private LocalDateTime time;
}
