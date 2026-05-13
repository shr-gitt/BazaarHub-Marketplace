package com.bazaarhub.backend.feature.vendorProfile.controller;

import com.bazaarhub.backend.feature.vendorProfile.resource.request.ApprovalRequestDto;
import com.bazaarhub.backend.feature.vendorProfile.resource.request.VendorRequestDto;
import com.bazaarhub.backend.feature.vendorProfile.resource.response.VendorResponseDto;
import com.bazaarhub.backend.feature.vendorProfile.service.VendorService;
import com.bazaarhub.backend.shared.aop.LogExecutionTime;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.utils.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VendorController {
    private final VendorService vendorService;

    @GetMapping("/vendors")
    @LogExecutionTime
    public ApiResponseDto<Page<VendorResponseDto>> getAllVendors(@PageableDefault(sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "All Vendors fetched", vendorService.getAllVendors(pageable));
    }

    @GetMapping("/vendors/my")
    @LogExecutionTime
    public ApiResponseDto<List<VendorResponseDto>> getVendorsByUser(){
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "All Vendors fetched by User.", vendorService.getVendorsByUser(AuthUtil.getCurrentUserId()));
    }

    @GetMapping("/vendor/{id}")
    @LogExecutionTime
    public ApiResponseDto<VendorResponseDto> getVendorById(@PathVariable Long id) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor fetched", vendorService.getVendorById(id));
    }

    @PostMapping("/vendor/create")
    @LogExecutionTime
    public ApiResponseDto<VendorResponseDto> createVendor(@RequestBody @Valid VendorRequestDto vendorRequestDto) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor created", vendorService.createVendor(AuthUtil.getCurrentUserId(), vendorRequestDto));
    }

    @PostMapping("/vendor/update/{id}")
    @LogExecutionTime
    public ApiResponseDto<VendorResponseDto> updateVendor(
            @PathVariable Long id,
            @RequestBody @Valid VendorRequestDto vendorRequestDto
    ) {

        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "Vendor updated.",
                vendorService.updateVendor(AuthUtil.getCurrentUserId(), id, vendorRequestDto)
        );
    }

    @PostMapping("/vendor/approval/{vendorId}")
    @LogExecutionTime
    public ApiResponseDto<VendorResponseDto> approveVendor(@PathVariable Long vendorId, @RequestBody @Valid ApprovalRequestDto approvalRequestDto) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor approval updated.", vendorService.approveVendor(AuthUtil.getCurrentUserId(), vendorId, approvalRequestDto));
    }

    @PostMapping("/vendor/delete/{id}")
    @LogExecutionTime
    public ApiResponseDto<Boolean> deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(AuthUtil.getCurrentUserId(), id);
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor deleted.");
    }
}
