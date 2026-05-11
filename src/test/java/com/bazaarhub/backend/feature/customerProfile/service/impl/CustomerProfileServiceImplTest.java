package com.bazaarhub.backend.feature.customerProfile.service.impl;

import com.bazaarhub.backend.feature.customerProfile.entity.CustomerProfile;
import com.bazaarhub.backend.feature.customerProfile.exception.CustomerProfileNotFoundException;
import com.bazaarhub.backend.feature.customerProfile.mapper.CustomerProfileMapper;
import com.bazaarhub.backend.feature.customerProfile.repository.CustomerProfileRepository;
import com.bazaarhub.backend.feature.customerProfile.resource.request.CustomerProfileRequestDTO;
import com.bazaarhub.backend.feature.customerProfile.resource.response.CustomerProfileResponseDTO;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import com.bazaarhub.backend.shared.service.MinioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerProfileServiceImplTest {

    @Mock
    private CustomerProfileRepository customerProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerProfileMapper customerProfileMapper;

    @Mock
    private MinioService minioService;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private CustomerProfileServiceImpl customerProfileService;

    private User user;
    private CustomerProfile customerProfile;
    private CustomerProfileRequestDTO customerProfileRequestDTO;
    private CustomerProfileResponseDTO customerProfileResponseDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        customerProfileRequestDTO = new CustomerProfileRequestDTO();
        customerProfileRequestDTO.setUserId(1L);
        customerProfileRequestDTO.setDateOfBirth(LocalDate.of(2000, 1, 1));
        customerProfileRequestDTO.setAddress("kathmandu");
        customerProfileRequestDTO.setPreferences(List.of(1, 2, 3));

        customerProfile = new CustomerProfile();
        customerProfile.setId(1L);
        customerProfile.setUser(user);
        customerProfile.setDateOfBirth(LocalDate.of(2000, 1, 1));
        customerProfile.setAddress("kathmandu");
        customerProfile.setPreferences(List.of(1, 2, 3));
        customerProfile.setProfileImageUrl("https://example.com/profile.jpg");

        customerProfileResponseDTO = new CustomerProfileResponseDTO(
                1L,
                0L,
                "Altaf",
                "Nepali",
                "altaf@gmail.com",
                null,
                "9800000000",
                "https://example.com/profile.jpg",
                LocalDate.of(2000, 1, 1),
                "kathmandu",
                List.of("Sports", "Music"),
                null,
                null
        );
    }

    @Test
    void createCustomerProfile_shouldCreateProfile_whenValidRequest() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(minioService.uploadFile(file))
                .thenReturn("https://example.com/profile.jpg");

        when(customerProfileMapper.mapToCustomerProfile(customerProfileRequestDTO))
                .thenReturn(customerProfile);

        when(customerProfileRepository.save(any(CustomerProfile.class)))
                .thenReturn(customerProfile);

        when(customerProfileMapper.mapToCustomerProfileResponseDTO(customerProfile))
                .thenReturn(customerProfileResponseDTO);

        CustomerProfileResponseDTO result =
                customerProfileService.createCustomerProfile(
                        customerProfileRequestDTO,
                        file
                );

        assertNotNull(result);

        assertEquals(
                "https://example.com/profile.jpg",
                result.getProfileImageUrl()
        );

        assertEquals(
                "kathmandu",
                result.getAddress()
        );

        assertEquals(
                List.of("Sports", "Music"),
                result.getPreferences()
        );

        verify(userRepository, times(1))
                .findById(1L);

        verify(minioService, times(1))
                .uploadFile(file);

        verify(customerProfileRepository, times(1))
                .save(any(CustomerProfile.class));

        verify(customerProfileMapper, times(1))
                .mapToCustomerProfileResponseDTO(customerProfile);
    }

    @Test
    void createCustomerProfile_shouldThrowException_whenUserNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> customerProfileService.createCustomerProfile(
                        customerProfileRequestDTO,
                        file
                )
        );

        verify(minioService, never())
                .uploadFile(any());

        verify(customerProfileRepository, never())
                .save(any(CustomerProfile.class));
    }

    @Test
    void getCustomerProfileById_shouldReturnProfile_whenFound() {

        when(customerProfileRepository.findById(1L))
                .thenReturn(Optional.of(customerProfile));

        when(customerProfileMapper.mapToCustomerProfileResponseDTO(customerProfile))
                .thenReturn(customerProfileResponseDTO);

        CustomerProfileResponseDTO result =
                customerProfileService.getCustomerProfileById(1L);

        assertNotNull(result);

        assertEquals(
                "Altaf",
                result.getFirstName()
        );

        assertEquals(
                "Nepali",
                result.getLastName()
        );

        assertEquals(
                "altaf@gmail.com",
                result.getEmail()
        );

        verify(customerProfileRepository, times(1))
                .findById(1L);

        verify(customerProfileMapper, times(1))
                .mapToCustomerProfileResponseDTO(customerProfile);
    }

    @Test
    void getCustomerProfileById_shouldThrowException_whenNotFound() {

        when(customerProfileRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerProfileNotFoundException.class,
                () -> customerProfileService.getCustomerProfileById(1L)
        );

        verify(customerProfileMapper, never())
                .mapToCustomerProfileResponseDTO(any(CustomerProfile.class));
    }

    @Test
    void updateCustomerProfileById_shouldUpdateProfile_whenValidRequest() {

        CustomerProfileRequestDTO updateRequest =
                new CustomerProfileRequestDTO();

        updateRequest.setDateOfBirth(
                LocalDate.of(2001, 5, 10)
        );

        updateRequest.setAddress("pokhara");

        updateRequest.setPreferences(
                List.of(1, 2)
        );

        CustomerProfileResponseDTO updatedResponse =
                new CustomerProfileResponseDTO(
                        1L,
                        0L,
                        "Altaf",
                        "Nepali",
                        "altaf@gmail.com",
                        null,
                        "9800000000",
                        "https://example.com/profile.jpg",
                        LocalDate.of(2001, 5, 10),
                        "Pokhara",
                        List.of("Travel", "Gaming"),
                        null,
                        null
                );

        when(customerProfileRepository.findById(1L))
                .thenReturn(Optional.of(customerProfile));

        when(customerProfileRepository.save(customerProfile))
                .thenReturn(customerProfile);

        when(customerProfileMapper.mapToCustomerProfileResponseDTO(customerProfile))
                .thenReturn(updatedResponse);

        CustomerProfileResponseDTO result =
                customerProfileService.updateCustomerProfileById(
                        1L,
                        updateRequest
                );

        assertNotNull(result);

        assertEquals(
                LocalDate.of(2001, 5, 10),
                customerProfile.getDateOfBirth()
        );

        assertEquals(
                "Pokhara",
                customerProfile.getAddress()
        );

        assertEquals(
                List.of(1, 2),
                customerProfile.getPreferences()
        );

        verify(customerProfileRepository, times(1))
                .findById(1L);

        verify(customerProfileRepository, times(1))
                .save(customerProfile);

        verify(customerProfileMapper, times(1))
                .mapToCustomerProfileResponseDTO(customerProfile);
    }

    @Test
    void updateCustomerProfileById_shouldThrowException_whenProfileNotFound() {

        when(customerProfileRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerProfileNotFoundException.class,
                () -> customerProfileService.updateCustomerProfileById(
                        99L,
                        customerProfileRequestDTO
                )
        );

        verify(customerProfileRepository, never())
                .save(any(CustomerProfile.class));
    }

    @Test
    void updateCustomerProfileById_shouldOnlyUpdateNonNullFields() {

        CustomerProfileRequestDTO updateRequest =
                new CustomerProfileRequestDTO();

        updateRequest.setDateOfBirth(null);

        updateRequest.setAddress("lalitpur");

        updateRequest.setPreferences(null);

        when(customerProfileRepository.findById(1L))
                .thenReturn(Optional.of(customerProfile));

        when(customerProfileRepository.save(customerProfile))
                .thenReturn(customerProfile);

        when(customerProfileMapper.mapToCustomerProfileResponseDTO(customerProfile))
                .thenReturn(customerProfileResponseDTO);

        customerProfileService.updateCustomerProfileById(
                1L,
                updateRequest
        );

        assertEquals(
                LocalDate.of(2000, 1, 1),
                customerProfile.getDateOfBirth()
        );

        assertEquals(
                "Lalitpur",
                customerProfile.getAddress()
        );

        assertEquals(
                List.of(1, 2, 3),
                customerProfile.getPreferences()
        );

        verify(customerProfileRepository, times(1))
                .save(customerProfile);
    }


}