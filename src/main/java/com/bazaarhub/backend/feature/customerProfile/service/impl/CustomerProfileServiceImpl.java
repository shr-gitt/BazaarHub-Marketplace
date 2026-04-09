package com.bazaarhub.backend.feature.customerProfile.service.impl;

import com.bazaarhub.backend.config.CacheConfig;
import com.bazaarhub.backend.feature.customerProfile.entity.CustomerProfile;
import com.bazaarhub.backend.feature.customerProfile.exception.CustomerProfileNotFoundException;
import com.bazaarhub.backend.feature.customerProfile.mapper.CustomerProfileMapper;
import com.bazaarhub.backend.feature.customerProfile.repository.CustomerProfileRepository;
import com.bazaarhub.backend.feature.customerProfile.resource.request.CustomerProfileRequestDTO;
import com.bazaarhub.backend.feature.customerProfile.resource.response.CustomerProfileResponseDTO;
import com.bazaarhub.backend.feature.customerProfile.service.CustomerProfileService;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import com.bazaarhub.backend.shared.utils.InputUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final UserRepository userRepository;
    private final CustomerProfileMapper customerProfileMapper;


    @Override
    @CachePut(cacheNames = CacheConfig.CUSTOMER_CACHE_NAME, key = "#userId")
    public CustomerProfileResponseDTO createCustomerProfile(Long userId, CustomerProfileRequestDTO customerProfileRequestDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Customer profile not found by id : {}", userId);
            return new UserNotFoundException("User Not Found");
        });
        CustomerProfile profile = customerProfileMapper.mapToCustomerProfile(customerProfileRequestDTO);
        profile.setProfileImageUrl(customerProfileRequestDTO.getProfileImageUrl());
        profile.setDateOfBirth(customerProfileRequestDTO.getDateOfBirth());
        profile.setAddress(customerProfileRequestDTO.getAddress());
        profile.setPreferences(customerProfileRequestDTO.getPreferences());

        profile.setUser(user);
        CustomerProfile saveProfile = customerProfileRepository.save(profile);
        return customerProfileMapper.mapToCustomerProfileResponseDTO(saveProfile);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.CUSTOMER_CACHE_NAME, key = "#id")
    public CustomerProfileResponseDTO getCustomerProfileById(Long id) {
        CustomerProfile profile = customerProfileRepository.findById(id).orElseThrow(() -> {
            log.error("Customer Profile Not Found.");
            return new CustomerProfileNotFoundException("Customer profile not found");
        });
        return customerProfileMapper.mapToCustomerProfileResponseDTO(profile);
    }


    @Override
    @CachePut(cacheNames = CacheConfig.CUSTOMER_CACHE_NAME, key = "#id")
    public CustomerProfileResponseDTO updateCustomerProfileById(Long id, CustomerProfileRequestDTO customerProfileRequestDTO) {
        CustomerProfile customerProfile = customerProfileRepository.findById(id).orElseThrow(() -> {
            log.error("Customer profile not found by id  : {}", id);
            return new CustomerProfileNotFoundException("Customer Profile Not Found");

        });
        String profileImageUrl = customerProfileRequestDTO.getProfileImageUrl();

        LocalDate dateOfBirth = customerProfileRequestDTO.getDateOfBirth();
        String address = InputUtil.capitalizeFirstLetter(customerProfileRequestDTO.getAddress());
        List<Integer> preferences = customerProfileRequestDTO.getPreferences();

        if (profileImageUrl != null) {
            customerProfile.setProfileImageUrl(profileImageUrl);
        }
        if (dateOfBirth != null) {
            customerProfile.setDateOfBirth(dateOfBirth);
        }

        if (preferences != null) {
            customerProfile.setPreferences(preferences);
        }
        if (address != null) {
            customerProfile.setAddress(address);
        }

        CustomerProfile updateProfile = customerProfileRepository.save(customerProfile);

        return customerProfileMapper.mapToCustomerProfileResponseDTO(updateProfile);
    }


}
