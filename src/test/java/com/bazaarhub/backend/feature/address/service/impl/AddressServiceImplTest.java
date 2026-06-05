package com.bazaarhub.backend.feature.address.service.impl;

import com.bazaarhub.backend.feature.address.entity.Address;
import com.bazaarhub.backend.feature.address.exception.AddressNotFoundException;
import com.bazaarhub.backend.feature.address.mapper.AddressMapper;
import com.bazaarhub.backend.feature.address.repository.AddressRepository;
import com.bazaarhub.backend.feature.address.resource.request.AddressRequestDto;
import com.bazaarhub.backend.feature.address.resource.response.AddressResponseDto;
import com.bazaarhub.backend.shared.enums.District;
import com.bazaarhub.backend.shared.enums.Municipality;
import com.bazaarhub.backend.shared.enums.Province;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    private Address address;
    private AddressRequestDto addressRequestDto;
    private AddressResponseDto addressResponseDto;

    @BeforeEach
    public void setUp() {
        address = new Address();
        address.setProvince(Province.BAGMATI);
        address.setDistrict(District.KATHMANDU);
        address.setMunicipality(Municipality.KATHMANDU_METROPOLITAN_CITY);
        address.setWardNo(10);
        address.setStreet("New Road");
        address.setPostalCode("44600");

        addressRequestDto = new AddressRequestDto();
        addressRequestDto.setProvince(Province.BAGMATI);
        addressRequestDto.setDistrict(District.KATHMANDU);
        addressRequestDto.setMunicipality(Municipality.KATHMANDU_METROPOLITAN_CITY);
        addressRequestDto.setWardNo(10);
        addressRequestDto.setStreet("New Road");
        addressRequestDto.setPostalCode("44600");

        addressResponseDto = new AddressResponseDto(
                1L,
                Province.BAGMATI,
                District.KATHMANDU,
                Municipality.KATHMANDU_METROPOLITAN_CITY,
                10,
                "New Road",
                "44600",
                null,
                null
        );
    }

    @Test
    void createAddress_shouldSaveAndReturnAddress() {
        when(addressMapper.mapToAddress(addressRequestDto)).thenReturn(address);
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(addressMapper.mapToAddressResponseDto(address)).thenReturn(addressResponseDto);

        Address result = addressService.createAddress(addressRequestDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Province.BAGMATI, result.getProvince());
        Assertions.assertEquals(District.KATHMANDU, result.getDistrict());
        Assertions.assertEquals(Municipality.KATHMANDU_METROPOLITAN_CITY, result.getMunicipality());
        Assertions.assertEquals(10, result.getWardNo());
        Assertions.assertEquals("New Road", result.getStreet());
        Assertions.assertEquals("44600", result.getPostalCode());

        verify(addressMapper, times(1)).mapToAddress(addressRequestDto);
        verify(addressRepository, times(1)).save(any(Address.class));
        verify(addressMapper, times(1)).mapToAddressResponseDto(address);
    }

    @Test
    void getAddressById_shouldReturnAddress_whenFound() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressMapper.mapToAddressResponseDto(address)).thenReturn(addressResponseDto);

        AddressResponseDto result = addressService.getAddressById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(District.KATHMANDU, result.getDistrict());
        Assertions.assertEquals("New Road", result.getStreet());

        verify(addressRepository, times(1)).findById(1L);
        verify(addressMapper, times(1)).mapToAddressResponseDto(address);
    }

    @Test
    void getAddressById_shouldThrowException_whenNotFound() {
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(AddressNotFoundException.class,
                () -> addressService.getAddressById(99L));

        verify(addressRepository, times(1)).findById(99L);
        verify(addressMapper, never()).mapToAddressResponseDto(any());
    }

    @Test
    void updateAddress_shouldUpdateAndReturnAddress() {
        AddressRequestDto updateRequest = new AddressRequestDto();
        updateRequest.setProvince(Province.GANDAKI);
        updateRequest.setDistrict(District.KASKI);
        updateRequest.setMunicipality(Municipality.POKHARA_METROPOLITAN_CITY);
        updateRequest.setWardNo(5);
        updateRequest.setStreet("Lakeside");
        updateRequest.setPostalCode("33700");

        AddressResponseDto updatedResponse = new AddressResponseDto(
                1L,
                Province.GANDAKI,
                District.KASKI,
                Municipality.POKHARA_METROPOLITAN_CITY,
                5,
                "Lakeside",
                "33700",
                null,
                null
        );

        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(addressMapper.mapToAddressResponseDto(address)).thenReturn(updatedResponse);

        AddressResponseDto result = addressService.updateAddress(1L, updateRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Province.GANDAKI, result.getProvince());
        Assertions.assertEquals(District.KASKI, result.getDistrict());
        Assertions.assertEquals(Municipality.POKHARA_METROPOLITAN_CITY, result.getMunicipality());
        Assertions.assertEquals(5, result.getWardNo());
        Assertions.assertEquals("Lakeside", result.getStreet());
        Assertions.assertEquals("33700", result.getPostalCode());

        verify(addressRepository, times(1)).findById(1L);
        verify(addressRepository, times(1)).save(any(Address.class));
        verify(addressMapper, times(1)).mapToAddressResponseDto(address);
    }

    @Test
    void updateAddress_shouldThrowException_whenNotFound() {
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(AddressNotFoundException.class,
                () -> addressService.updateAddress(99L, addressRequestDto));

        verify(addressRepository, times(1)).findById(99L);
        verify(addressRepository, never()).save(any(Address.class));
        verify(addressMapper, never()).mapToAddressResponseDto(any());
    }

    @Test
    void deleteAddress_shouldDeleteAddress_whenFound() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

        addressService.deleteAddress(1L);

        verify(addressRepository, times(1)).findById(1L);
        verify(addressRepository, times(1)).delete(address);
    }

    @Test
    void deleteAddress_shouldThrowException_whenNotFound() {
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(AddressNotFoundException.class,
                () -> addressService.deleteAddress(99L));

        verify(addressRepository, times(1)).findById(99L);
        verify(addressRepository, never()).delete(any(Address.class));
    }

}

