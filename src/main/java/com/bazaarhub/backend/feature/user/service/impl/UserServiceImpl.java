package com.bazaarhub.backend.feature.user.service.impl;

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
        String email = TextUtil.normalizeEmail(userRequestDto.getEmail());
        String phoneNumber = userRequestDto.getPhoneNumber();
        if (userRepository.existsByEmailAndUserStatusNot(email, UserStatus.DELETED)) {
            log.error("Email already in used : {}", email);
            throw new EmailAlreadyExistsException("Email already exists.");
        }
        if (userRepository.existsByPhoneNumberAndUserStatusNot(phoneNumber, UserStatus.DELETED)) {
            log.error("Phone number already in use: {}", phoneNumber);
            throw new PhoneNumberAlreadyExistsException("Phone number already exists");
        }
        User user = UserMapper.mapToUser(userRequestDto);
        user.setFirstName(TextUtil.capitalizeFirstLetter(userRequestDto.getFirstName()));
        user.setLastName(TextUtil.capitalizeFirstLetter(userRequestDto.getLastName()));
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
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
        User user = userRepository.findByIdAndUserStatusNot(userId, UserStatus.DELETED).orElseThrow(() -> {
            log.error("User not found {}", userId);
            return new UserNotFoundException("User not found");
        });
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
        log.info("Fetching user with id: {}}", userId);
        User user = userRepository.findByIdAndUserStatusNot(userId, UserStatus.DELETED).orElseThrow(() -> {
            log.error("User not found for id : {}", userId);
            return new UserNotFoundException("User Not found");
        });
        String firstName = TextUtil.capitalizeFirstLetter(userRequestDto.getFirstName());
        String lastName = TextUtil.capitalizeFirstLetter(userRequestDto.getLastName());
        String email = TextUtil.normalizeEmail(userRequestDto.getEmail());
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
        log.info("User updated [id= {}]", userId);
        return UserMapper.mapToUserResponse(updatedUser);
    }

    @Override
    @CacheEvict(cacheNames = CACHE_NAME, key = "#userId")
    public void deleteUserById(Long userId) {
        log.info("Deleting user [id= {}]", userId);
        User user = userRepository.findByIdAndUserStatusNot(userId, UserStatus.DELETED).orElseThrow(() -> {
            log.error("User not found {}", userId);
            return new UserNotFoundException("User Not Found");
        });
        user.setUserStatus(UserStatus.DELETED);
        userRepository.save(user);
        log.info("User deleted [id={}, status={}]", userId, user.getUserStatus());
    }
}
