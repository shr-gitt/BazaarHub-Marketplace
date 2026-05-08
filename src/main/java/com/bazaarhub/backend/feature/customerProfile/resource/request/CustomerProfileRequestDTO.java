package com.bazaarhub.backend.feature.customerProfile.resource.request;

import com.bazaarhub.backend.feature.address.resource.request.AddressRequestDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerProfileRequestDTO implements Serializable {

    @NotNull(message = "Date of Birth is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotNull(message = "Address is required")
    private AddressRequestDto addressRequestDto;

    @NotEmpty(message = "Preference is required")
    private List<Integer> preferences = new ArrayList<>();
}
