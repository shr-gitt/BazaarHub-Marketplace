package com.bazaarhub.backend.feature.auth.service.serviceimpl;

import com.bazaarhub.backend.feature.auth.mapper.AuthMapper;
import com.bazaarhub.backend.feature.auth.resources.request.LoginRequestDto;
import com.bazaarhub.backend.feature.auth.resources.request.RegisterRequestDto;
import com.bazaarhub.backend.feature.auth.resources.response.AuthResponse;
import com.bazaarhub.backend.feature.auth.security.CustomUserDetailsService;
import com.bazaarhub.backend.feature.auth.security.JwtService;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.exception.EmailAlreadyExistsException;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import com.bazaarhub.backend.shared.enums.Role;
import com.bazaarhub.backend.shared.enums.UserStatus;
import com.bazaarhub.backend.shared.exception.InvalidCredentialException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private RegisterRequestDto registerRequestDto;
    private LoginRequestDto loginRequestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);
        user.setUserStatus(UserStatus.ACTIVE);

        registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setEmail("test@gmail.com");
        registerRequestDto.setPassword("password123");
        registerRequestDto.setRole(Role.CUSTOMER);

        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail(" TEST@gmail.com ");
        loginRequestDto.setPassword("password123");
    }

    @Test
    void registerUser_shouldRegisterUser_whenEmailDoesNotExist() {
        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        when(authMapper.mapToUser(registerRequestDto))
                .thenReturn(user);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        AuthResponse result = authService.registerUser(registerRequestDto);

        assertNotNull(result);
        assertEquals("Registration Successful", result.getMessage());
        assertEquals(1L, result.getUserId());
        assertEquals(Role.CUSTOMER, result.getRole());
        assertNull(result.getToken());

        assertEquals(UserStatus.ACTIVE, user.getUserStatus());
        assertEquals("test@gmail.com", user.getEmail());
        assertEquals(Role.CUSTOMER, user.getRole());
        assertEquals("encodedPassword", user.getPassword());

        verify(userRepository, times(1)).findByEmail("test@gmail.com");
        verify(authMapper, times(1)).mapToUser(registerRequestDto);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void registerUser_shouldThrowException_whenEmailAlreadyExists() {
        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.registerUser(registerRequestDto)
        );

        verify(authMapper, never()).mapToUser(any(RegisterRequestDto.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_shouldLoginUser_whenCredentialsAreValid() {
        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);

        when(customUserDetailsService.loadUserByUsername("test@gmail.com"))
                .thenReturn(userDetails);

        when(jwtService.generateToken(
                eq(userDetails),
                eq(Map.of("userId", 1L, "role", Role.CUSTOMER))
        )).thenReturn("jwt-token");

        AuthResponse result = authService.loginUser(loginRequestDto);

        assertNotNull(result);
        assertEquals("Login Successful", result.getMessage());
        assertEquals(1L, result.getUserId());
        assertEquals(Role.CUSTOMER, result.getRole());
        assertEquals("jwt-token", result.getToken());

        verify(userRepository, times(1)).findByEmail("test@gmail.com");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
        verify(customUserDetailsService, times(1)).loadUserByUsername("test@gmail.com");
        verify(jwtService, times(1)).generateToken(
                eq(userDetails),
                eq(Map.of("userId", 1L, "role", Role.CUSTOMER))
        );
    }

    @Test
    void loginUser_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> authService.loginUser(loginRequestDto)
        );

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).generateToken(any(), anyMap());
    }

    @Test
    void loginUser_shouldThrowException_whenPasswordIsInvalid() {
        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialException.class,
                () -> authService.loginUser(loginRequestDto)
        );

        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).generateToken(any(), anyMap());
    }

}