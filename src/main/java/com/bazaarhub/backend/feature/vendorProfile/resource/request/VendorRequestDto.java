package com.bazaarhub.backend.feature.vendorProfile.resource.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigInteger;

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

    @NotBlank(message = "Address is required.")
    private String address;

    @NotBlank(message = "City is required.")
    private String city;

    @NotBlank(message = "Country is required.")
    private String country;
}
