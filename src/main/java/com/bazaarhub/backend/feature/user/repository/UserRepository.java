package com.bazaarhub.backend.feature.user.repository;

import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.shared.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndUserStatusNot(Long id, UserStatus status);

    boolean existsByEmailAndUserStatusNot(String email, UserStatus status);

    boolean existsByPhoneNumberAndUserStatusNot(String phoneNumber, UserStatus status);
}
