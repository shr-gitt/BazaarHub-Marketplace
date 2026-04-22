package com.bazaarhub.backend.feature.customerProfile.resource.response;

import com.bazaarhub.backend.feature.user.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerProfileResponseDTO implements Serializable {
    private Long id;
    private Long version;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private String phoneNumber;
    private String profileImageUrl;
    private LocalDate dateOfBirth;
    private String address;
    private List<String> preferences;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
