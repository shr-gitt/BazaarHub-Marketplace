package com.bazaarhub.backend.feature.customerProfile.mapper;

import com.bazaarhub.backend.feature.customerProfile.entity.CustomerProfile;
import com.bazaarhub.backend.feature.customerProfile.enums.Preferences;
import com.bazaarhub.backend.feature.customerProfile.resource.request.CustomerProfileRequestDTO;
import com.bazaarhub.backend.feature.customerProfile.resource.response.CustomerProfileResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerProfileMapper {
    public CustomerProfile mapToCustomerProfile(CustomerProfileRequestDTO customerProfileRequestDTO) {
        CustomerProfile customerProfile = new CustomerProfile();

        customerProfile.setProfileImageUrl(customerProfileRequestDTO.getProfileImageUrl());
        customerProfile.setDateOfBirth(customerProfileRequestDTO.getDateOfBirth());
        customerProfile.setAddress(customerProfileRequestDTO.getAddress());
        customerProfile.setPreferences(customerProfileRequestDTO.getPreferences());
        return customerProfile;
    }

    public CustomerProfileResponseDTO mapToCustomerProfileResponseDTO(CustomerProfile customerProfile) {
        return new CustomerProfileResponseDTO(

                customerProfile.getId(),
                customerProfile.getVersion(),
                customerProfile.getUser().getId(),
                customerProfile.getUser().getFirstName(),
                customerProfile.getUser().getLastName(),
                customerProfile.getUser().getEmail(),
                customerProfile.getUser().getGender(),
                customerProfile.getUser().getPhoneNumber(),
                customerProfile.getProfileImageUrl(),
                customerProfile.getDateOfBirth(),
                customerProfile.getAddress(),
                customerProfile.getPreferences()
                        .stream()
                        .map(Preferences::fromId)
                        .map(Enum::name)
                        .collect(Collectors.toList()),
                customerProfile.getCreatedAt(),
                customerProfile.getModifiedAt()


        );
    }
}
