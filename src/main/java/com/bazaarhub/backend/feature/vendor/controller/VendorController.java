package com.bazaarhub.backend.feature.vendor.controller;

import com.bazaarhub.backend.feature.vendor.resource.request.ApprovalRequestDto;
import com.bazaarhub.backend.feature.vendor.resource.request.VendorRequestDto;
import com.bazaarhub.backend.feature.vendor.resource.response.VendorResponseDto;
import com.bazaarhub.backend.feature.vendor.service.VendorService;
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
    public ApiResponseDto<Page<VendorResponseDto>> getAllVendors(@PageableDefault(sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<VendorResponseDto> vendorResponseDtos = vendorService.getAllVendors(pageable);

        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "All Vendors fetched", vendorResponseDtos);
    }

    @GetMapping("/vendor/{id}")
    public ResponseEntity<ApiResponseDto<VendorResponseDto>> getVendorById(@PathVariable Long id) {
        VendorResponseDto vendorResponseDto = vendorService.getVendorById(id);

        return new ResponseEntity<>(
                new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor fetched", vendorResponseDto),
                HttpStatus.OK
        );
    }

    @PostMapping("/vendor/create/{userId}")
    public ResponseEntity<ApiResponseDto<VendorResponseDto>> createVendor(@PathVariable Long userId, @RequestBody @Valid VendorRequestDto vendorRequestDto){
        VendorResponseDto vendorResponseDto = vendorService.createVendor(userId, vendorRequestDto);

        return new ResponseEntity<>(
                new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor created", vendorResponseDto),
                HttpStatus.OK
        );
    }

    @PostMapping("/vendor/update/{vendorId}")
    public ResponseEntity<ApiResponseDto<VendorResponseDto>> updateVendor(@PathVariable Long vendorId, @RequestBody @Valid VendorRequestDto vendorRequestDto){
        VendorResponseDto vendorResponseDto = vendorService.updateVendor(vendorId, vendorRequestDto);

        return new ResponseEntity<>(
                new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor updated.", vendorResponseDto),
                HttpStatus.OK
        );
    }

    @PostMapping("/vendor/approval/{vendorId}")
    public ResponseEntity<ApiResponseDto<VendorResponseDto>> vendorApproval(@PathVariable Long vendorId, @RequestBody @Valid ApprovalRequestDto approvalRequestDto){
        VendorResponseDto approvalResponseDto = vendorService.vendorApproval(vendorId, approvalRequestDto);

        return new ResponseEntity<>(
                new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor approval updated.", approvalResponseDto),
                HttpStatus.OK
        );
    }

    @PostMapping("/vendor/delete/{id}")
    public ResponseEntity<ApiResponseDto<Boolean>> deleteVendor(@PathVariable Long id){
        boolean res = vendorService.deleteVendor(id);

        return new ResponseEntity<>(
                new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor deleted.", res),
                HttpStatus.OK
        );
    }
}
