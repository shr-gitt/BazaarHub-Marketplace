package com.bazaarhub.backend.feature.customerProfile.repository;

import com.bazaarhub.backend.feature.customerProfile.entity.CustomerProfile;
import com.bazaarhub.backend.feature.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {

    //for recommendation
    Optional<CustomerProfile> findByUser_Id(Long userId);

    boolean existsByUser(User user);
}
