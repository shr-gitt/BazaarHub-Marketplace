package com.bazaarhub.backend.feature.customerProfile.service;

import com.bazaarhub.backend.feature.customerProfile.resource.request.CustomerProfileRequestDTO;
import com.bazaarhub.backend.feature.customerProfile.resource.response.CustomerProfileResponseDTO;

public interface CustomerProfileService {
    CustomerProfileResponseDTO createCustomerProfile(Long userId, CustomerProfileRequestDTO customerProfileRequestDTO);

    CustomerProfileResponseDTO getCustomerProfileById(Long id);

    CustomerProfileResponseDTO updateCustomerProfileById(Long id, CustomerProfileRequestDTO customerProfileRequestDTO);

}
