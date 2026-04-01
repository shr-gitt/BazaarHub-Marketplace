package com.bazaarhub.backend.feature.user.resource.response;

import com.bazaarhub.backend.feature.user.enums.Gender;
import com.bazaarhub.backend.shared.enums.Role;
import com.bazaarhub.backend.shared.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto implements Serializable {
    private Long id;
    private Long version;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Gender gender;
    private Role role;
    private UserStatus userStatus;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedDate;
}
