package com.bazaarhub.backend.feature.auth.controller;

import com.bazaarhub.backend.feature.auth.resources.request.LoginRequestDto;
import com.bazaarhub.backend.feature.auth.resources.request.RegisterRequestDto;
import com.bazaarhub.backend.feature.auth.resources.response.AuthResponse;
import com.bazaarhub.backend.feature.auth.service.AuthService;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class RegisterController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponseDto<AuthResponse> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        AuthResponse response = authService.registerUser(registerRequestDto);
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Register Successful", response);

    }

    @PostMapping("/login")
    public ApiResponseDto<AuthResponse> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        AuthResponse response = authService.loginUser(loginRequestDto);
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Login successful", response);
    }

}
