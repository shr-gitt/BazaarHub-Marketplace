package com.bazaarhub.backend.feature.customerProfile.controller;

import com.bazaarhub.backend.feature.customerProfile.resource.request.CustomerProfileRequestDTO;
import com.bazaarhub.backend.feature.customerProfile.resource.response.CustomerProfileResponseDTO;
import com.bazaarhub.backend.feature.customerProfile.service.CustomerProfileService;

import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;

    @PostMapping("/create-customer-profile")
    public ApiResponseDto<CustomerProfileResponseDTO> createCustomerProfile(@Valid @RequestBody CustomerProfileRequestDTO customerProfileRequestDTO) {
        CustomerProfileResponseDTO response = customerProfileService.createCustomerProfile(customerProfileRequestDTO);
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Customer Profile created successfully", response);
    }

    @GetMapping("/customer-profile/{id}")
    public ApiResponseDto<CustomerProfileResponseDTO> getCustomerProfileById(@PathVariable Long id) {
        CustomerProfileResponseDTO response = customerProfileService.getCustomerProfileById(id);
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Customer Profile fetched successfully", response);
    }

    @PostMapping("/update-customer-profile/{id}")
    public ApiResponseDto<CustomerProfileResponseDTO> updateCustomerProfileById(@PathVariable long id, @Valid @RequestBody CustomerProfileRequestDTO customerProfileRequestDTO) {
        CustomerProfileResponseDTO response = customerProfileService.updateCustomerProfileById(id, customerProfileRequestDTO);
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Customer Profile Updated", response);
    }
}
