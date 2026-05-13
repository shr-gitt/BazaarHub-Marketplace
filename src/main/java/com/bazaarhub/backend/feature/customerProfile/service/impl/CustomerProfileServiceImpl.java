package com.bazaarhub.backend.feature.customerProfile.service.impl;

import com.bazaarhub.backend.config.CacheConfig;
import com.bazaarhub.backend.feature.address.entity.Address;
import com.bazaarhub.backend.feature.address.repository.AddressRepository;
import com.bazaarhub.backend.feature.address.service.AddressService;
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
import com.bazaarhub.backend.shared.exception.UnauthorizedAccessException;
import com.bazaarhub.backend.shared.service.MinioService;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final UserRepository userRepository;
    private final CustomerProfileMapper customerProfileMapper;
    private final MinioService minioService;
    private final AddressService addressService;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    @CachePut(cacheNames = CacheConfig.CUSTOMER_CACHE_NAME, key = "#result.id")
    public CustomerProfileResponseDTO createCustomerProfile(Long userId, CustomerProfileRequestDTO customerProfileRequestDTO, MultipartFile file) {
        log.info("Creating vendor profile for userId={}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User not found by id : {}", userId);
            return new UserNotFoundException("User not found with given id.");
        });

        if(customerProfileRepository.existsByUser(user)){
            log.error("Customer profile creation failed. Profile with userId={} already exists.", userId);
            throw new EntityExistsException("Profile already exists.");
        }

        String imageUrl = minioService.uploadFile(file);

        Address address = addressRepository.findByStreetAndMunicipality(
                customerProfileRequestDTO.getAddressRequestDto().getStreet(),
                customerProfileRequestDTO.getAddressRequestDto().getMunicipality()
        ).orElseGet(() ->
                addressService.createAddress(
                        customerProfileRequestDTO.getAddressRequestDto()
                )
        );

        CustomerProfile profile = customerProfileMapper.mapToCustomerProfile(customerProfileRequestDTO, address);
        profile.setUser(user);
        profile.setProfileImageUrl(imageUrl);
        CustomerProfile savedProfile = customerProfileRepository.save(profile);
        return customerProfileMapper.mapToCustomerProfileResponseDTO(savedProfile);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.CUSTOMER_CACHE_NAME, key = "#id")
    public CustomerProfileResponseDTO getCustomerProfileById(Long id) {
        CustomerProfile profile = customerProfileRepository.findById(id).orElseThrow(() -> {
            log.error("Customer Profile Not Found.");
            return new CustomerProfileNotFoundException("Customer profile not found with given id.");
        });
        return customerProfileMapper.mapToCustomerProfileResponseDTO(profile);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.CUSTOMER_CACHE_NAME, key = "#id")
    public CustomerProfileResponseDTO getCustomerProfileByUser(Long userId) {
        CustomerProfile profile = customerProfileRepository.findByUserId(userId);
        return customerProfileMapper.mapToCustomerProfileResponseDTO(profile);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CacheConfig.CUSTOMER_CACHE_NAME, key = "#id")
    public CustomerProfileResponseDTO updateCustomerProfileById(Long userId, Long id, CustomerProfileRequestDTO customerProfileRequestDTO) {
        log.info("Updating customer profile of id={}", id);

        CustomerProfile customerProfile = customerProfileRepository.findById(id).orElseThrow(() -> {
            log.error("Customer profile fetch failed. Profile not found with id={}", id);
            return new CustomerProfileNotFoundException("Customer profile not found with given id.");
        });

        if(!customerProfile.getUser().getId().equals(userId)){
            log.error("Customer profile update failed. due to unauthorized access. userId={}, profileId={}",
                    userId,
                    id);
            throw new UnauthorizedAccessException("You are not authorized to update this customer profile.");
        }

        LocalDate dateOfBirth = customerProfileRequestDTO.getDateOfBirth();
        List<Integer> preferences = customerProfileRequestDTO.getPreferences();

        if (customerProfileRequestDTO.getAddressRequestDto() != null) {
            Address address = addressRepository.findByStreetAndMunicipality(
                    customerProfileRequestDTO.getAddressRequestDto().getStreet(),
                    customerProfileRequestDTO.getAddressRequestDto().getMunicipality()
            ).orElse(
                    addressService.createAddress(customerProfileRequestDTO.getAddressRequestDto())
            );

            customerProfile.setAddress(address);
        }

        if (dateOfBirth != null) {
            customerProfile.setDateOfBirth(dateOfBirth);
        }

        if (preferences != null) {
            customerProfile.setPreferences(preferences);
        }

        CustomerProfile updatedProfile = customerProfileRepository.save(customerProfile);

        return customerProfileMapper.mapToCustomerProfileResponseDTO(updatedProfile);
    }
}
