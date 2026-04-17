package com.bazaarhub.backend.feature.auth.service;

import com.bazaarhub.backend.feature.auth.resources.request.LoginRequestDto;
import com.bazaarhub.backend.feature.auth.resources.request.RegisterRequestDto;
import com.bazaarhub.backend.feature.auth.resources.response.AuthResponse;

public interface AuthService {
    AuthResponse registerUser(RegisterRequestDto registerRequestDto);

    AuthResponse loginUser(LoginRequestDto loginRequestDto);
}
