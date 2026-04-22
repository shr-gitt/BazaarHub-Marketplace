package com.bazaarhub.backend.feature.address.resource.request;

import com.bazaarhub.backend.shared.enums.District;
import com.bazaarhub.backend.shared.enums.Municipality;
import com.bazaarhub.backend.shared.enums.Province;
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
public class AddressRequestDto implements Serializable {

    @NotNull(message = "Province is required")
    private Province province;

    @NotNull(message = "District is required")
    private District district;

    @NotNull(message = "Municipality is required")
    private Municipality municipality;

    @NotNull(message = "Ward number is required")
    @Min(value = 1, message = "Ward number must be at least 1")
    @Max(value = 35, message = "Ward number must not exceed 35")
    private Integer wardNo;

    @NotBlank(message = "Street is required")
    @Size(max = 255, message = "Street must not exceed 255 characters")
    private String street;

    @NotBlank(message = "Postal code is required")
    @Size(max = 10, message = "Postal code must not exceed 10 characters")
    private String postalCode;
}