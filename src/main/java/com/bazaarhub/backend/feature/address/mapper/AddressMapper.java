package com.bazaarhub.backend.feature.address.mapper;

import com.bazaarhub.backend.feature.address.entity.Address;
import com.bazaarhub.backend.feature.address.resource.request.AddressRequestDto;
import com.bazaarhub.backend.feature.address.resource.response.AddressResponseDto;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address mapToAddress(AddressRequestDto dto) {
        Address address = new Address();
        address.setProvince(dto.getProvince());
        address.setDistrict(dto.getDistrict());
        address.setMunicipality(dto.getMunicipality());
        address.setWardNo(dto.getWardNo());
        address.setStreet(dto.getStreet().trim());
        address.setPostalCode(dto.getPostalCode().trim());
        return address;
    }

    public AddressResponseDto mapToAddressResponseDto(Address address) {
        return new AddressResponseDto(
                address.getId(),
                address.getProvince(),
                address.getDistrict(),
                address.getMunicipality(),
                address.getWardNo(),
                address.getStreet(),
                address.getPostalCode(),
                address.getCreatedAt(),
                address.getModifiedAt()
        );
    }
}