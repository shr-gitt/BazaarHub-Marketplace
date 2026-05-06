package com.bazaarhub.backend.feature.vendorProfile.repository;

import com.bazaarhub.backend.feature.vendorProfile.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {

    Optional<Vendor> findByBusinessPhone(String businessPhone);

    Optional<Vendor> findByBusinessEmail(String businessEmail);

    Optional<Vendor> findByPanCardNo(String panCardNo);

    Optional<Vendor> findByRegistrationNo(String registrationNo);

    Optional<Vendor> findByUserId(Long userId);
}
