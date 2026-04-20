package com.bazaarhub.backend.feature.auth.service.serviceimpl;

import com.bazaarhub.backend.feature.auth.mapper.AuthMapper;
import com.bazaarhub.backend.feature.auth.resources.request.LoginRequestDto;
import com.bazaarhub.backend.feature.auth.resources.request.RegisterRequestDto;
import com.bazaarhub.backend.feature.auth.resources.response.AuthResponse;
import com.bazaarhub.backend.feature.auth.security.CustomUserDetailsService;
import com.bazaarhub.backend.feature.auth.security.JwtService;
import com.bazaarhub.backend.feature.auth.service.AuthService;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.exception.EmailAlreadyExistsException;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import com.bazaarhub.backend.shared.enums.UserStatus;
import com.bazaarhub.backend.shared.exception.InvalidCredentialException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor

public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    @Override
    public AuthResponse registerUser(RegisterRequestDto registerRequestDto) {
        String email = registerRequestDto.getEmail();
        userRepository.findByEmail(email).ifPresent(user -> {
            log.error("Email already exists");
            throw new EmailAlreadyExistsException("Email Already Exists");
        });
        User user = authMapper.mapToUser(registerRequestDto);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setEmail(email);
        user.setRole(registerRequestDto.getRole());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        userRepository.save(user);
        return new AuthResponse("Registration Successful", user.getId(), user.getRole(), null);
    }

    @Override
    public AuthResponse loginUser(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User not found of email: {}", email);
            return new UserNotFoundException("User not found");
        });

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialException("Invalid credentials");
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        String token = jwtService.generateToken(userDetails, Map.of("userId", user.getId(), "role", user.getRole() != null ? user.getRole() : "USER"));
        return new AuthResponse("Login Successful", user.getId(), user.getRole(), token);
    }
}


