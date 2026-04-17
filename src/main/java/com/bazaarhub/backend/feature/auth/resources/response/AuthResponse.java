package com.bazaarhub.backend.feature.auth.resources.response;

import com.bazaarhub.backend.shared.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse implements Serializable {
    private String message;
    private Long userId;
    private Role role;
    private String token;
}
