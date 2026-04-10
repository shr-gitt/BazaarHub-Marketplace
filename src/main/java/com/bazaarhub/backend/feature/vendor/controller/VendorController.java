package com.bazaarhub.backend.feature.vendor.controller;

import com.bazaarhub.backend.feature.vendor.resource.request.ApprovalRequestDto;
import com.bazaarhub.backend.feature.vendor.resource.request.VendorRequestDto;
import com.bazaarhub.backend.feature.vendor.resource.response.VendorResponseDto;
import com.bazaarhub.backend.feature.vendor.service.VendorService;
import com.bazaarhub.backend.shared.aop.LogExecutionTime;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VendorController {
    private final VendorService vendorService;

    @GetMapping("/vendors")
    @LogExecutionTime
    public ApiResponseDto<Page<VendorResponseDto>> getAllVendors(@PageableDefault(sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable){
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "All Vendors fetched", vendorService.getAllVendors(pageable));
    }

    @GetMapping("/vendor/{id}")
    @LogExecutionTime
    public ApiResponseDto<VendorResponseDto> getVendorById(@PathVariable Long id) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor fetched", vendorService.getVendorById(id));
    }

    @PostMapping("/vendor/create/{userId}")
    @LogExecutionTime
    public ApiResponseDto<VendorResponseDto> createVendor(@PathVariable Long userId, @RequestBody @Valid VendorRequestDto vendorRequestDto){
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor created", vendorService.createVendor(userId, vendorRequestDto));
    }

    @PostMapping("/vendor/update/{vendorId}")
    @LogExecutionTime
    public ApiResponseDto<VendorResponseDto> updateVendor(@PathVariable Long vendorId, @RequestBody @Valid VendorRequestDto vendorRequestDto){
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor updated.", vendorService.updateVendor(vendorId, vendorRequestDto));
    }

    @PostMapping("/vendor/approval/{vendorId}")
    @LogExecutionTime
    public ApiResponseDto<VendorResponseDto> vendorApproval(@PathVariable Long vendorId, @RequestBody @Valid ApprovalRequestDto approvalRequestDto){
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor approval updated.", vendorService.vendorApproval(vendorId, approvalRequestDto));
    }

    @PostMapping("/vendor/delete/{id}")
    @LogExecutionTime
    public ApiResponseDto<Boolean> deleteVendor(@PathVariable Long id){
        vendorService.deleteVendor(id);
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor deleted.");
    }
}
