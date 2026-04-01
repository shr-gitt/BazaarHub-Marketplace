package com.bazaarhub.backend.feature.user.resource.request;


import com.bazaarhub.backend.feature.user.enums.Gender;
import com.bazaarhub.backend.shared.enums.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto implements Serializable {

    @NotBlank(message = "Firstname is required")
    @Size(min = 2, max = 30, message = "Firstname must be between 2 and 30 characters")
    @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$", message = "Firstname must contain only letters")
    private String firstName;

    @NotBlank(message = "Lastname is required")
    @Size(min = 2, max = 30, message = "Lastname must be between 2 and 30 characters")
    @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$", message = "Lastname must contain only letters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "Gender shouldn't be empty")
    private Gender gender;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 16, message = "Password must be 8–16 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
            message = "Password must contain uppercase, lowercase, number, and special character"
    )
    private String password;

    @NotNull(message = "Role shouldn't be empty")
    private Role role;
}
