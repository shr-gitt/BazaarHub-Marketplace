package com.bazaarhub.backend.feature.auth.resources.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto implements Serializable {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 16, message = "Password must be 8-16")
    private String password;

}
