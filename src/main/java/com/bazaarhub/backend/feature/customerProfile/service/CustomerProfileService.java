package com.bazaarhub.backend.feature.customerProfile.service;

import com.bazaarhub.backend.feature.customerProfile.resource.request.CustomerProfileRequestDTO;
import com.bazaarhub.backend.feature.customerProfile.resource.response.CustomerProfileResponseDTO;
import com.bazaarhub.backend.feature.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerProfileService {
    CustomerProfileResponseDTO createCustomerProfile(Long userId, CustomerProfileRequestDTO customerProfileRequestDTO, MultipartFile file);

    CustomerProfileResponseDTO getCustomerProfileById(Long id);

    CustomerProfileResponseDTO getCustomerProfileByUser(Long userId);

    CustomerProfileResponseDTO updateCustomerProfileById(Long userId, Long id, CustomerProfileRequestDTO customerProfileRequestDTO);
}
