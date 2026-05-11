package com.bazaarhub.backend.feature.customerProfile.repository;

import com.bazaarhub.backend.feature.customerProfile.entity.CustomerProfile;
import com.bazaarhub.backend.feature.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {

    //for recommendation
    CustomerProfile findByUserId(Long userId);

    boolean existsByUser(User user);
}
