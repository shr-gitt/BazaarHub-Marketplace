package com.bazaarhub.backend.feature.customerProfile.controller;

import com.bazaarhub.backend.feature.customerProfile.resource.request.CustomerProfileRequestDTO;
import com.bazaarhub.backend.feature.customerProfile.resource.response.CustomerProfileResponseDTO;
import com.bazaarhub.backend.feature.customerProfile.service.CustomerProfileService;

import com.bazaarhub.backend.shared.aop.LogExecutionTime;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import com.bazaarhub.backend.shared.utils.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;

    @PostMapping(value = "/create-customer-profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @LogExecutionTime
    public ApiResponseDto<CustomerProfileResponseDTO> createCustomerProfile(@Valid
                                                                            @RequestPart("customerProfileDto") CustomerProfileRequestDTO customerProfileRequestDTO,
                                                                            @RequestPart("file") MultipartFile file) {
        CustomerProfileResponseDTO response = customerProfileService.createCustomerProfile(AuthUtil.getCurrentUserId(), customerProfileRequestDTO, file);
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Customer Profile created successfully", response);
    }

    @GetMapping("/customer-profile/{id}")
    @LogExecutionTime
    public ApiResponseDto<CustomerProfileResponseDTO> getCustomerProfileById(@PathVariable Long id) {
        CustomerProfileResponseDTO response = customerProfileService.getCustomerProfileById(id);
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Customer Profile fetched successfully", response);
    }

    @PostMapping("/update-customer-profile/{id}")
    @LogExecutionTime
    public ApiResponseDto<CustomerProfileResponseDTO> updateCustomerProfileById(@PathVariable long id, @Valid @RequestBody CustomerProfileRequestDTO customerProfileRequestDTO) {
        CustomerProfileResponseDTO response = customerProfileService.updateCustomerProfileById(AuthUtil.getCurrentUserId(), id, customerProfileRequestDTO);
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Customer Profile Updated", response);
    }
}
