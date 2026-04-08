package com.bazaarhub.backend.feature.vendor.service;

import com.bazaarhub.backend.feature.vendor.resource.request.ApprovalRequestDto;
import com.bazaarhub.backend.feature.vendor.resource.request.VendorRequestDto;
import com.bazaarhub.backend.feature.vendor.resource.response.VendorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VendorService {
    Page<VendorResponseDto> getAllVendors(Pageable pageable);

    VendorResponseDto getVendorById(Long id);

    VendorResponseDto createVendor(Long userId, VendorRequestDto vendorRequestDto);

    VendorResponseDto updateVendor(Long vendorId, VendorRequestDto vendorRequestDto);

    VendorResponseDto vendorApproval(Long vendorId, ApprovalRequestDto approvalRequestDto);

    boolean deleteVendor (Long vendorId);
}
