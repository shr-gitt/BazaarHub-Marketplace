package com.bazaarhub.backend.shared.utils;

import com.bazaarhub.backend.feature.auth.security.CustomUserDetails;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public final class AuthUtil {

    public static CustomUserDetails getCurrentUser() {
        return (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }


    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

//    public Long getCurrentUserId() {
//        String email = getCurrentUserEmail();
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserNotFoundException("User not found."))
//                .getId();
//    }

//    public User getCurrentUser() {
//        String email = getCurrentUserEmail();
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> {
//                    log.error("User not found of email: {}", email);
//                    return new UserNotFoundException("User not found.");
//                });
//    }
//
//    public Long getCurrentUserId() {
//        return getCurrentUser().getId();
//    }

}
