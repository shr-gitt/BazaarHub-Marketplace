package com.bazaarhub.backend.feature.vendorProfile.service;

import com.bazaarhub.backend.feature.vendorProfile.resource.request.ApprovalRequestDto;
import com.bazaarhub.backend.feature.vendorProfile.resource.request.VendorRequestDto;
import com.bazaarhub.backend.feature.vendorProfile.resource.response.VendorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VendorService {
    Page<VendorResponseDto> getAllVendors(Pageable pageable);

    List<VendorResponseDto> getVendorsByUser(Long userId);

    VendorResponseDto getVendorById(Long id);

    VendorResponseDto createVendor(Long userId, VendorRequestDto vendorRequestDto);

    VendorResponseDto updateVendor(Long userId, Long vendorId, VendorRequestDto vendorRequestDto);

    VendorResponseDto approveVendor(Long approverId,Long vendorId, ApprovalRequestDto approvalRequestDto);

    void deleteVendor (Long userId, Long vendorId);
}
