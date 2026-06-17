package com.bazaarhub.backend.feature.address.controller;

import com.bazaarhub.backend.feature.address.resource.response.AddressResponseDto;
import com.bazaarhub.backend.feature.address.service.AddressService;
import com.bazaarhub.backend.shared.aop.LogExecutionTime;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/address/{id}")
    @LogExecutionTime
    public ApiResponseDto<AddressResponseDto> getAddressById(
            @PathVariable("id") Long id) {
        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "Address fetched successfully",
                addressService.getAddressById(id));
    }

    @GetMapping("/addresses")
    @LogExecutionTime
    public ApiResponseDto<Page<AddressResponseDto>> getAllAddresses(
            @PageableDefault(sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "Addresses fetched successfully",
                addressService.getAllAddresses(pageable));
    }
}