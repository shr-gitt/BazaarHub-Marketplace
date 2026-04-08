package com.bazaarhub.backend.feature.vendor.repository;

import com.bazaarhub.backend.feature.vendor.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {

    Optional<Vendor> findByBusinessPhone(String businessPhone);

    Optional<Vendor> findByBusinessEmail(String businessEmail);

    Optional<Vendor> findByPanCardNo(String panCardNo);

    Optional<Vendor> findByRegistrationNo(String registrationNo);
}
