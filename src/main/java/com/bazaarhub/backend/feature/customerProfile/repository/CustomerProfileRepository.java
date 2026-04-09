package com.bazaarhub.backend.feature.customerProfile.repository;

import com.bazaarhub.backend.feature.customerProfile.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {
}
