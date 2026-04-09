package com.bazaarhub.backend.feature.user.service;

import com.bazaarhub.backend.feature.user.resource.request.UserRequestDto;
import com.bazaarhub.backend.feature.user.resource.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);
    UserResponseDto getUserById(Long userId);
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    UserResponseDto updateUserById(Long userId, UserRequestDto userRequestDto);
    void deleteUserById(Long userId);
}
