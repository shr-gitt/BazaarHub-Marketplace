package com.bazaarhub.backend.feature.vendorProfile.resource.request;

import com.bazaarhub.backend.feature.address.resource.request.AddressRequestDto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class VendorRequestDto implements Serializable {

    @NotBlank(message = "Vendor shop name is required.")
    @Size(min = 2, max = 30, message = "Vendor shop name must be between 2 and 30 letters")
    private String shopName;

    @NotBlank(message = "Business Email is required.")
    @Email(message = "Invalid Email")
    private String businessEmail;

    @NotBlank(message = "Business Phone Number is required.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Business Phone must be a 10-digit number")
    private String businessPhone;

    @NotBlank(message = "Business Pan Card No is required.")
    private String panCardNo;

    @NotBlank(message = "Business registration number is required.")
    private String registrationNo;

    @NotNull(message = "Address is required.")
    private AddressRequestDto addressRequestDto;
}
