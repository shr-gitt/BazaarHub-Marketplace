package com.bazaarhub.backend.feature.user.service.impl;

import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.user.mapper.UserMapper;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import com.bazaarhub.backend.feature.user.resource.request.UserRequestDto;
import com.bazaarhub.backend.feature.user.resource.response.UserResponseDto;
import com.bazaarhub.backend.feature.user.service.UserService;
import com.bazaarhub.backend.shared.enums.UserStatus;
import com.bazaarhub.backend.shared.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String CACHE_NAME = "user";

    @Override
    @CachePut(cacheNames = CACHE_NAME, key = "#result.id")
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = UserMapper.mapToUser(userRequestDto);
        user.setFirstName(TextUtil.capitalizeFirstLetter(userRequestDto.getFirstName()));
        user.setLastName(TextUtil.capitalizeFirstLetter(userRequestDto.getLastName()));
        user.setEmail(TextUtil.normalizeEmail(userRequestDto.getEmail()));
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setUserStatus(UserStatus.ACTIVE);
        User newUser = userRepository.save(user);
        log.info("User created [id={}, email={}]", newUser.getId(), newUser.getEmail());
        return UserMapper.mapToUserResponse(newUser);
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME, key = "#userId")
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId).filter(u -> u.getUserStatus() != UserStatus.DELETED).orElseThrow(() -> new UserNotFoundException("User not found"));
        log.info("Fetched user [id= {}]", userId);
        return UserMapper.mapToUserResponse(user);
    }

    @Override
    public Page<UserResponseDto> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable).map(UserMapper::mapToUserResponse);
    }

    @Override
    @CachePut(cacheNames = CACHE_NAME, key = "#userId")
    public UserResponseDto updateUserById(Long userId, UserRequestDto userRequestDto) {
        log.info("Fetching user [id= {}]", userId);
        User user = userRepository.findById(userId).filter(u -> u.getUserStatus() != UserStatus.DELETED).orElseThrow(() -> {
            log.info("User not found {}", userId);
            return new UserNotFoundException("User Not found");
        });
        if (userRequestDto.getFirstName() != null) {
            user.setFirstName(TextUtil.capitalizeFirstLetter(userRequestDto.getFirstName()));
        }
        if (userRequestDto.getLastName() != null) {
            user.setLastName(TextUtil.capitalizeFirstLetter(userRequestDto.getLastName()));
        }
        if (userRequestDto.getEmail() != null) {
            user.setEmail(TextUtil.normalizeEmail(userRequestDto.getEmail()));
        }
        if (userRequestDto.getPhoneNumber() != null) {
            user.setPhoneNumber(userRequestDto.getPhoneNumber());
        }
        if (userRequestDto.getGender() != null) {
            user.setGender(userRequestDto.getGender());
        }
        if (userRequestDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        }
        if (userRequestDto.getRole() != null) {
            user.setRole(userRequestDto.getRole());
        }
        User updatedUser = userRepository.save(user);
        log.info("User updated [id= {}]", userId);
        return UserMapper.mapToUserResponse(updatedUser);
    }

    @Override
    @CacheEvict(cacheNames = CACHE_NAME, key = "#userId")
    public void deleteUserById(Long userId) {
        log.info("Deleting user [id= {}]", userId);
        User user = userRepository.findById(userId).filter(u -> u.getUserStatus() != UserStatus.DELETED).orElseThrow(() -> {
            log.info("User not found {}", userId);
            return new UserNotFoundException("User Not Found");
        });
        user.setUserStatus(UserStatus.DELETED);
        userRepository.save(user);
        log.info("User deleted [id={}, status={}]", userId, user.getUserStatus());
    }
}
