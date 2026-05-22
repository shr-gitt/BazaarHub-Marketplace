package com.bazaarhub.backend.feature.user.controller;

import com.bazaarhub.backend.feature.user.resource.request.UserRequestDto;
import com.bazaarhub.backend.feature.user.resource.response.UserResponseDto;
import com.bazaarhub.backend.feature.user.service.UserService;
import com.bazaarhub.backend.shared.aop.LogExecutionTime;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.enums.Role;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register-user")
    @LogExecutionTime
    public ApiResponseDto<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "User created successfully", userService.createUser(userRequestDto));
    }

    @GetMapping("/user/{id}")
    @LogExecutionTime
    public ApiResponseDto<UserResponseDto> getUserById(@PathVariable("id") Long userId) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "User fetched successfully", userService.getUserById(userId));
    }

    @GetMapping("/users")
    @LogExecutionTime
    public ApiResponseDto<Page<UserResponseDto>> getAllUsers(@PageableDefault(sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Users fetched successfully", userService.getAllUsers(pageable));
    }

    @PostMapping("/update-user/{id}")
    @LogExecutionTime
    public ApiResponseDto<UserResponseDto> updateUserById(@PathVariable("id") Long userId, @Valid @RequestBody UserRequestDto userRequestDto) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "User updated successfully", userService.updateUserById(userId, userRequestDto));
    }

    @DeleteMapping("/user/{id}")
    @LogExecutionTime
    public ApiResponseDto<String> deleteUserById(@PathVariable("id") Long userId) {
        userService.deleteUserById(userId);
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "User deleted successfully");
    }

    @PostMapping("/admin/create")
    @LogExecutionTime
    public ApiResponseDto<UserResponseDto> createAdmin(
            @Valid @RequestBody UserRequestDto userRequestDto
    ) {
        userRequestDto.setRole(Role.ADMIN);

        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "Admin created successfully",
                userService.createUser(userRequestDto)
        );
    }

}
