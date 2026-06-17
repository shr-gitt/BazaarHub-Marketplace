package com.bazaarhub.backend.feature.auth.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AuthExpiredTokens {
    @NotNull
    private String token;
}
