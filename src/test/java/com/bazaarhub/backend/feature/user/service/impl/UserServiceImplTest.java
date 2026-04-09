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
import com.bazaarhub.backend.shared.enums.Role;
import com.bazaarhub.backend.shared.enums.UserStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Sampanna");
        user.setLastName("Sapkota");
        user.setEmail("sampanna@gmail.com");
        user.setPhoneNumber("9812345678");
        user.setGender(Gender.MALE);
        user.setPassword("encodedPassword");
        user.setRole(Role.ADMIN);
        user.setUserStatus(UserStatus.ACTIVE);

        userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("sampanna");
        userRequestDto.setLastName("sapkota");
        userRequestDto.setEmail("Sampanna@gmail.com");
        userRequestDto.setPhoneNumber("9812345678");
        userRequestDto.setGender(Gender.MALE);
        userRequestDto.setPassword("plainPassword");
        userRequestDto.setRole(Role.ADMIN);

        userResponseDto = new UserResponseDto(
                user.getId(),
                user.getVersion(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getGender(),
                user.getRole(),
                user.getUserStatus(),
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }

    @Test
    void createUser_shouldNormalizeAndPrepareUserCorrectly() {

        when(userRepository.existsByEmailAndUserStatusNot("sampanna@gmail.com", UserStatus.DELETED)).thenReturn(false);
        when(userRepository.existsByPhoneNumberAndUserStatusNot("9812345678", UserStatus.DELETED)).thenReturn(false);
        when(userMapper.mapToUser(userRequestDto)).thenReturn(user);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.mapToUserResponse(user)).thenReturn(userResponseDto);

        UserResponseDto newUser = userService.createUser(userRequestDto);

        Assertions.assertNotNull(newUser);
        Assertions.assertEquals(user.getEmail(), newUser.getEmail());
        Assertions.assertEquals(user.getPhoneNumber(), newUser.getPhoneNumber());
        Assertions.assertEquals(user.getUserStatus(), newUser.getUserStatus());
        Assertions.assertEquals(user.getRole(), newUser.getRole());
        Assertions.assertEquals(user.getFirstName(), newUser.getFirstName());
        Assertions.assertEquals(user.getLastName(), newUser.getLastName());

        verify(userRepository, times(1))
                .existsByEmailAndUserStatusNot("sampanna@gmail.com", UserStatus.DELETED);
        verify(userRepository, times(1))
                .existsByPhoneNumberAndUserStatusNot("9812345678", UserStatus.DELETED);
        verify(userMapper, times(1)).mapToUser(userRequestDto);
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).mapToUserResponse(user);
    }

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {
        when(userRepository.existsByEmailAndUserStatusNot("sampanna@gmail.com", UserStatus.DELETED)).thenReturn(true);
        Assertions.assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(userRequestDto));
        verify(userRepository, times(1))
                .existsByEmailAndUserStatusNot("sampanna@gmail.com", UserStatus.DELETED);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).mapToUser(any());
        verify(userMapper, never()).mapToUserResponse(any());
    }

    @Test
    void createUser_shouldThrowException_whenPhoneNumberAlreadyExists() {
        when(userRepository.existsByEmailAndUserStatusNot("sampanna@gmail.com", UserStatus.DELETED)).thenReturn(false);
        when(userRepository.existsByPhoneNumberAndUserStatusNot("9812345678", UserStatus.DELETED)).thenReturn(true);

        Assertions.assertThrows(PhoneNumberAlreadyExistsException.class, () -> userService.createUser(userRequestDto));

        verify(userRepository, times(1)).existsByEmailAndUserStatusNot("sampanna@gmail.com", UserStatus.DELETED);
        verify(userRepository, times(1)).existsByPhoneNumberAndUserStatusNot("9812345678", UserStatus.DELETED);
        verify(userRepository, times(1))
                .existsByEmailAndUserStatusNot("sampanna@gmail.com", UserStatus.DELETED);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).mapToUser(any());
        verify(userMapper, never()).mapToUserResponse(any());
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findByIdAndUserStatusNot(1L, UserStatus.DELETED)).thenReturn(Optional.of(user));
        when(userMapper.mapToUserResponse(user)).thenReturn(userResponseDto);

        UserResponseDto response = userService.getUserById(1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(user.getEmail(), response.getEmail());
        Assertions.assertEquals(user.getFirstName(), response.getFirstName());
        Assertions.assertEquals(user.getLastName(), response.getLastName());

        verify(userRepository, times(1)).findByIdAndUserStatusNot(1L, UserStatus.DELETED);
        verify(userMapper, times(1)).mapToUserResponse(user);
    }

    @Test
    void getUserById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByIdAndUserStatusNot(1L, UserStatus.DELETED)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));

        verify(userRepository, times(1)).findByIdAndUserStatusNot(1L, UserStatus.DELETED);
        verify(userMapper, never()).mapToUserResponse(any());
    }

    @Test
    void getAllUsers_shouldReturnPagedUsers() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "modifiedAt"));
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.mapToUserResponse(user)).thenReturn(userResponseDto);

        Page<UserResponseDto> responsePage = userService.getAllUsers(pageable);

        Assertions.assertNotNull(responsePage);
        Assertions.assertEquals(1, responsePage.getTotalElements());
        Assertions.assertEquals(1, responsePage.getContent().size());
        Assertions.assertEquals(user.getEmail(), responsePage.getContent().get(0).getEmail());

        verify(userRepository, times(1)).findAll(pageable);
        verify(userMapper, times(1)).mapToUserResponse(user);
    }


}