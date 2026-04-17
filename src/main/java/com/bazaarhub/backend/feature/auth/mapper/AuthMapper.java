package com.bazaarhub.backend.feature.auth.mapper;

import com.bazaarhub.backend.feature.auth.resources.request.RegisterRequestDto;
import com.bazaarhub.backend.feature.auth.resources.response.AuthResponse;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.shared.utils.InputUtil;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public User mapToUser(RegisterRequestDto registerRequestDto) {
        User user = new User();
        user.setFirstName(InputUtil.capitalizeFirstLetter(registerRequestDto.getFirstName()));
        user.setLastName(InputUtil.capitalizeFirstLetter(registerRequestDto.getLastName()));
        user.setEmail(registerRequestDto.getEmail());
        user.setPhoneNumber(registerRequestDto.getPhoneNumber());
        user.setGender(registerRequestDto.getGender());
        user.setPassword(registerRequestDto.getPassword());
        user.setRole(registerRequestDto.getRole());
        return user;
    }

    public AuthResponse mapToAuthResponse(String message, User user, String token) {
        return new AuthResponse(message, user.getId(), user.getRole(), token);
    }
}
