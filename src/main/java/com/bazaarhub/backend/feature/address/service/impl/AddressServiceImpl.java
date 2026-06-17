package com.bazaarhub.backend.feature.address.service.impl;

import com.bazaarhub.backend.config.CacheConfig;
import com.bazaarhub.backend.feature.address.entity.Address;
import com.bazaarhub.backend.feature.address.exception.AddressNotFoundException;
import com.bazaarhub.backend.feature.address.mapper.AddressMapper;
import com.bazaarhub.backend.feature.address.repository.AddressRepository;
import com.bazaarhub.backend.feature.address.resource.request.AddressRequestDto;
import com.bazaarhub.backend.feature.address.resource.response.AddressResponseDto;
import com.bazaarhub.backend.feature.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    @CachePut(cacheNames = CacheConfig.ADDRESS_CACHE_NAME, key = "#result.id")
    public Address createAddress(AddressRequestDto addressRequestDto) {
        log.info("Creating address");
        return addressRepository.save(addressMapper.mapToAddress(addressRequestDto));
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.ADDRESS_CACHE_NAME, key = "#id")
    public AddressResponseDto getAddressById(Long id) {
        log.info("Fetching address [addressId={}]", id);
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Address not found [addressId={}]", id);
                    return new AddressNotFoundException(id);
                });
        return addressMapper.mapToAddressResponseDto(address);
    }

    @Override
    public Page<AddressResponseDto> getAllAddresses(Pageable pageable) {
        log.info("Fetching all addresses");
        return addressRepository.findAll(pageable)
                .map(addressMapper::mapToAddressResponseDto);
    }

    @Override
    @CachePut(cacheNames = CacheConfig.ADDRESS_CACHE_NAME, key = "#id")
    public AddressResponseDto updateAddress(Long id, AddressRequestDto addressRequestDto) {
        log.info("Updating address [addressId={}]", id);
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Address not found [addressId={}]", id);
                    return new AddressNotFoundException(id);
                });
        address.setProvince(addressRequestDto.getProvince());
        address.setDistrict(addressRequestDto.getDistrict());
        address.setMunicipality(addressRequestDto.getMunicipality());
        address.setWardNo(addressRequestDto.getWardNo());
        address.setStreet(addressRequestDto.getStreet());
        address.setPostalCode(addressRequestDto.getPostalCode());
        return addressMapper.mapToAddressResponseDto(addressRepository.save(address));
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.ADDRESS_CACHE_NAME, key = "#id")
    public void deleteAddress(Long id) {
        log.info("Deleting address [addressId={}]", id);
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Address not found [addressId={}]", id);
                    return new AddressNotFoundException(id);
                });
        addressRepository.delete(address);
    }
}