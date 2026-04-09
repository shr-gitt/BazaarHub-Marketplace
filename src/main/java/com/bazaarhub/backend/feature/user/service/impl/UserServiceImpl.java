package com.bazaarhub.backend.feature.user.service.impl;

import com.bazaarhub.backend.config.CacheConfig;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.enums.Gender;
import com.bazaarhub.backend.feature.user.exception.EmailAlreadyExistsException;
import com.bazaarhub.backend.feature.user.exception.PhoneNumberAlreadyExistsException;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.user.mapper.UserMapper;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import com.bazaarhub.backend.feature.user.resource.request.UserRequestDto;
import com.bazaarhub.backend.feature.user.resource.response.UserResponseDto;
import com.bazaarhub.backend.feature.user.service.UserService;
import com.bazaarhub.backend.shared.enums.Role;
import com.bazaarhub.backend.shared.enums.UserStatus;
import com.bazaarhub.backend.shared.utils.InputUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private static final String CACHE_NAME = "user";

    @Override
    @CachePut(cacheNames = CacheConfig.USER_CACHE_NAME, key = "#result.id")
    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        String email = InputUtil.normalizeEmail(userRequestDto.getEmail());
        String phoneNumber = userRequestDto.getPhoneNumber();

        if (userRepository.existsByEmailAndUserStatusNot(email, UserStatus.DELETED)) {
            log.error("Email already in used : {}", email);
            throw new EmailAlreadyExistsException("Email already exists.");
        }
        if (userRepository.existsByPhoneNumberAndUserStatusNot(phoneNumber, UserStatus.DELETED)) {
            log.error("Phone number already in use: {}", phoneNumber);
            throw new PhoneNumberAlreadyExistsException("Phone number already exists");
        }

        User user = userMapper.mapToUser(userRequestDto);
        user.setFirstName(InputUtil.capitalizeFirstLetter(userRequestDto.getFirstName()));
        user.setLastName(InputUtil.capitalizeFirstLetter(userRequestDto.getLastName()));
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setUserStatus(UserStatus.ACTIVE);
        User newUser = userRepository.save(user);

        return userMapper.mapToUserResponse(newUser);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.USER_CACHE_NAME, key = "#userId")
    public UserResponseDto getUserById(Long userId) {

        User user = userRepository.findByIdAndUserStatusNot(userId, UserStatus.DELETED).orElseThrow(() -> {
            log.error("User not found {}", userId);
            return new UserNotFoundException("User not found");
        });
        return userMapper.mapToUserResponse(user);
    }

    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {

        return userRepository.findAll(pageable).map(userMapper::mapToUserResponse);
    }

    @Override
    @CachePut(cacheNames = CacheConfig.USER_CACHE_NAME, key = "#userId")
    public UserResponseDto updateUserById(Long userId, UserRequestDto userRequestDto) {

        User user = userRepository.findByIdAndUserStatusNot(userId, UserStatus.DELETED).orElseThrow(() -> {
            log.error("User not found for id : {}", userId);
            return new UserNotFoundException("User Not found");
        });

        String firstName = InputUtil.capitalizeFirstLetter(userRequestDto.getFirstName());
        String lastName = InputUtil.capitalizeFirstLetter(userRequestDto.getLastName());
        String email = InputUtil.normalizeEmail(userRequestDto.getEmail());
        String phoneNumber = userRequestDto.getPhoneNumber();
        Gender gender = userRequestDto.getGender();
        String password = passwordEncoder.encode(userRequestDto.getPassword());
        Role role = userRequestDto.getRole();

        if (firstName != null) {
            user.setFirstName(firstName);
        }

        if (lastName != null) {
            user.setLastName(lastName);
        }

        if (email != null) {
            if (userRepository.existsByEmailAndUserStatusNot(email, UserStatus.DELETED)) {
                log.error("Email already in used : {}", email);
                throw new EmailAlreadyExistsException("Email already in use.");
            }

            user.setEmail(email);
        }
        if (phoneNumber != null) {
            if (userRepository.existsByPhoneNumberAndUserStatusNot(phoneNumber, UserStatus.DELETED)) {
                log.error("Phone number already in use : {}", phoneNumber);
                throw new PhoneNumberAlreadyExistsException("Phone number already in use.");
            }

            user.setPhoneNumber(phoneNumber);
        }
        if (gender != null) {
            user.setGender(gender);
        }

        if (password != null) {
            user.setPassword(password);
        }

        if (role != null) {
            user.setRole(role);
        }

        User updatedUser = userRepository.save(user);
        return userMapper.mapToUserResponse(updatedUser);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.USER_CACHE_NAME, key = "#userId")
    public void deleteUserById(Long userId) {

        User user = userRepository.findByIdAndUserStatusNot(userId, UserStatus.DELETED).orElseThrow(() -> {
            log.error("User not found {}", userId);
            return new UserNotFoundException("User Not Found");
        });

        user.setUserStatus(UserStatus.DELETED);
        userRepository.save(user);

        log.info("User deleted [id={}, status={}]", userId, user.getUserStatus());
    }
}
