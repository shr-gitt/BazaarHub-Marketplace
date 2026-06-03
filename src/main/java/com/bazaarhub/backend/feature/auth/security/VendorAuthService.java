package com.bazaarhub.backend.feature.auth.security;

import com.bazaarhub.backend.feature.vendorProfile.enums.ApprovalStatus;
import com.bazaarhub.backend.feature.vendorProfile.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("vendorAuth")
@RequiredArgsConstructor
public class VendorAuthService {
    private final VendorRepository vendorRepository;

    public boolean isApproved(Long vendorId) {
        return vendorRepository.findById(vendorId)
                .map(v -> v.getApprovalStatus() == ApprovalStatus.APPROVED)
                .orElse(false);
    }
}
