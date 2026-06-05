package com.bazaarhub.backend.feature.user.repository;

import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.shared.enums.Role;
import com.bazaarhub.backend.shared.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndUserStatusNot(Long id, UserStatus status);

    boolean existsByEmailAndUserStatusNot(String email, UserStatus status);

    Optional<User> findByEmail(String email);

    boolean existsByPhoneNumberAndUserStatusNot(String phoneNumber, UserStatus status);

    List<User> findByRole(Role role);
}
