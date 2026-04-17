package com.bazaarhub.backend.feature.auth.security;

import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private User user;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User not found of email: {}", email);
            return new UserNotFoundException("User not found");
        });
        String role = user.getRole() != null ? user.getRole().name() : "USER";
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail()).password(user.getPassword()).authorities(authority).accountLocked(false).disabled(false).build();

    }
}

