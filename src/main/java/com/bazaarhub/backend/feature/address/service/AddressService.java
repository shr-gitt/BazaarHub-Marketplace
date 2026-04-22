package com.bazaarhub.backend.feature.address.service;

import com.bazaarhub.backend.feature.address.resource.request.AddressRequestDto;
import com.bazaarhub.backend.feature.address.resource.response.AddressResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AddressService {

    AddressResponseDto createAddress(AddressRequestDto addressRequestDto);

    AddressResponseDto getAddressById(Long id);

    Page<AddressResponseDto> getAllAddresses(Pageable pageable);

    AddressResponseDto updateAddress(Long id, AddressRequestDto addressRequestDto);

    void deleteAddress(Long id);
}