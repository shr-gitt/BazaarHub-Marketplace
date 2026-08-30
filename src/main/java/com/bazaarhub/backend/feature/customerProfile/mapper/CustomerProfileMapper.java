package com.bazaarhub.backend.feature.customerProfile.mapper;

import com.bazaarhub.backend.feature.address.entity.Address;
import com.bazaarhub.backend.feature.address.mapper.AddressMapper;
import com.bazaarhub.backend.feature.customerProfile.entity.CustomerProfile;
import com.bazaarhub.backend.feature.customerProfile.enums.Preferences;
import com.bazaarhub.backend.feature.customerProfile.resource.request.CustomerProfileRequestDTO;
import com.bazaarhub.backend.feature.customerProfile.resource.response.CustomerProfileResponseDTO;
import com.bazaarhub.backend.shared.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerProfileMapper {
    private final AddressMapper addressMapper;
    private final MinioService minioService;

    public CustomerProfile mapToCustomerProfile(CustomerProfileRequestDTO customerProfileRequestDTO, Address address) {
        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setDateOfBirth(customerProfileRequestDTO.getDateOfBirth());
        customerProfile.setAddress(address);
        customerProfile.setPreferences(customerProfileRequestDTO.getPreferences());
        return customerProfile;
    }

    public CustomerProfileResponseDTO mapToCustomerProfileResponseDTO(CustomerProfile customerProfile) {
        return new CustomerProfileResponseDTO(

                customerProfile.getId(),
                customerProfile.getVersion(),
                customerProfile.getUser().getFirstName(),
                customerProfile.getUser().getLastName(),
                customerProfile.getUser().getEmail(),
                customerProfile.getUser().getGender(),
                customerProfile.getUser().getPhoneNumber(),
                minioService.getImageUrl(customerProfile.getProfileImageUrl()),
                customerProfile.getDateOfBirth(),
                addressMapper.mapToAddressResponseDto(customerProfile.getAddress()),
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
