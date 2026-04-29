package com.bazaarhub.backend.feature.customerProfile.service;

import com.bazaarhub.backend.feature.customerProfile.resource.request.CustomerProfileRequestDTO;
import com.bazaarhub.backend.feature.customerProfile.resource.response.CustomerProfileResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerProfileService {
    CustomerProfileResponseDTO createCustomerProfile(CustomerProfileRequestDTO customerProfileRequestDTO, MultipartFile file);

    CustomerProfileResponseDTO getCustomerProfileById(Long id);

    CustomerProfileResponseDTO updateCustomerProfileById(Long id, CustomerProfileRequestDTO customerProfileRequestDTO);

}
