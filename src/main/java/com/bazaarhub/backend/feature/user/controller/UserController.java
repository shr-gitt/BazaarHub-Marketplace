package com.bazaarhub.backend.feature.user.controller;

import com.bazaarhub.backend.feature.user.resource.request.UserRequestDto;
import com.bazaarhub.backend.feature.user.resource.response.UserResponseDto;
import com.bazaarhub.backend.feature.user.service.UserService;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register-user")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponse = userService.createUser(userRequestDto);
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "User created successfully", userResponse);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getUserById(@PathVariable("id") Long userId) {
        UserResponseDto userResponse = userService.getUserById(userId);
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "User fetched successfully", userResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponseDto<Page<UserResponseDto>>> getAllUsers() {
        Page<UserResponseDto> allUsers = userService.getAllUsers(0, 10);
        ApiResponseDto<Page<UserResponseDto>> response = new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Users fetched successfully", allUsers);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/{id}")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> updateUserById(@Valid @PathVariable("id") Long userId, @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto user = userService.updateUserById(userId, userRequestDto);
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "User updated successfully", user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<ApiResponseDto<String>> deleteUserById(@PathVariable("id") Long userId) {
        userService.deleteUserById(userId);
        ApiResponseDto<String> response = new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "User deleted successfully");
        return ResponseEntity.ok(response);
    }
}
