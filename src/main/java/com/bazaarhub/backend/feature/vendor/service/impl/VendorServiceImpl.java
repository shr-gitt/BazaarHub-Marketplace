package com.bazaarhub.backend.feature.vendor.service.impl;

import com.bazaarhub.backend.config.CacheConfig;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import com.bazaarhub.backend.feature.vendor.resource.request.ApprovalRequestDto;
import com.bazaarhub.backend.feature.vendor.resource.request.VendorRequestDto;
import com.bazaarhub.backend.feature.vendor.resource.response.VendorResponseDto;
import com.bazaarhub.backend.feature.vendor.entity.Vendor;
import com.bazaarhub.backend.feature.vendor.enums.ApprovalStatus;
import com.bazaarhub.backend.feature.vendor.enums.VendorProfileStatus;
import com.bazaarhub.backend.feature.vendor.exception.VendorInactiveException;
import com.bazaarhub.backend.feature.vendor.exception.VendorNotFoundException;
import com.bazaarhub.backend.feature.vendor.mapper.VendorMapper;
import com.bazaarhub.backend.feature.vendor.repository.VendorRepository;
import com.bazaarhub.backend.feature.vendor.service.VendorService;
import com.bazaarhub.backend.shared.utils.TextUtil;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    private final VendorMapper vendorMapper;

    private final UserRepository userRepository;

    @Override
    public Page<VendorResponseDto> getAllVendors(Pageable pageable) {
        log.info("Fetching all vendors.");
        Pageable pages = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        return vendorRepository.findAll(pages)
                .map(vendorMapper::mapToVendorResponse);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.VENDOR_CACHE_NAME, key = "#id")
    public VendorResponseDto getVendorById(Long id){
        log.info("Fetching vendor with: [id={}] in getVendorById",id);
        Vendor vendor = vendorRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Could not find vendor with vendor id: [id={}] in getVendorById",id);
                    return new VendorNotFoundException("Vendor not found.");
                }
        );

        if(vendor.getVendorProfileStatus() == VendorProfileStatus.ACTIVE)
            return vendorMapper.mapToVendorResponse(vendor);
        else {
            log.error("Vendor is inactive.");
            throw new VendorInactiveException("Vendor is not active.");
        }
    }

    @Override
    @CachePut(cacheNames = CacheConfig.VENDOR_CACHE_NAME, key = "#result.id")
    public VendorResponseDto createVendor(Long userId, VendorRequestDto vendorRequestDto){
        log.info("Trying to create vendor.");
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.error("User not found in create vendor.");
                    throw new UserNotFoundException("User not found.");
                }
        );

        Vendor vendor = vendorMapper.mapToVendor(vendorRequestDto);

        Optional<Vendor> existingVendorOpt = vendorRepository.findByBusinessEmail(vendor.getBusinessEmail());

        if(existingVendorOpt.isEmpty())
            existingVendorOpt = vendorRepository.findByBusinessPhone(vendor.getBusinessPhone());

        if(existingVendorOpt.isEmpty())
            existingVendorOpt = vendorRepository.findByPanCardNo(vendor.getPanCardNo());

        if(existingVendorOpt.isEmpty())
            existingVendorOpt = vendorRepository.findByRegistrationNo(vendor.getRegistrationNo());

        if( existingVendorOpt.isPresent()){
            Vendor existingVendor = existingVendorOpt.get();

            if (VendorProfileStatus.ACTIVE == existingVendor.getVendorProfileStatus()){
                log.error("Vendor with this information is active so cannot be created");
                throw new EntityExistsException("Vendor account with this information already exists. Use other credentials.");
            }

            vendor = vendorMapper.mapToVendor(vendorRequestDto, existingVendor);
        }

        vendor.setUser(user);

        vendor.setBusinessEmail(TextUtil.normalizeEmail(vendor.getBusinessEmail()));

        vendor.setVendorProfileStatus(VendorProfileStatus.ACTIVE);

        vendor.setApprovalStatus(ApprovalStatus.PENDING);

        Vendor newVendor = vendorRepository.save(vendor);

        return vendorMapper.mapToVendorResponse(newVendor);
    }

    @Override
    @CachePut(cacheNames = CacheConfig.VENDOR_CACHE_NAME, key = "#vendorId")
    public VendorResponseDto updateVendor(Long vendorId, VendorRequestDto vendorRequestDto){

        log.info("Fetching vendor with: [id={}] in updateVendor",vendorId);

        Vendor toUpdateVendor = vendorRepository.findById(vendorId).orElseThrow(
                () -> {
                    log.error("Could not find vendor with vendor id: [id={}] in updateVendor",vendorId);
                    return new VendorNotFoundException("Vendor not found to update.");
                }
        );

        Vendor vendor = vendorMapper.mapToVendor(vendorRequestDto);

        toUpdateVendor.setShopName(vendor.getShopName());

        toUpdateVendor.setBusinessEmail(TextUtil.normalizeEmail(vendor.getBusinessEmail()));

        toUpdateVendor.setBusinessPhone(vendor.getBusinessPhone());

        toUpdateVendor.setAddress(vendor.getAddress());

        toUpdateVendor.setCity(vendor.getCity());

        toUpdateVendor.setCountry(vendor.getCountry());

        Vendor updatedVendor = vendorRepository.save(toUpdateVendor);

        return vendorMapper.mapToVendorResponse(updatedVendor);
    }

    @Override
    @CachePut(cacheNames = CacheConfig.VENDOR_CACHE_NAME, key = "#vendorId")
    public VendorResponseDto vendorApproval(Long vendorId, ApprovalRequestDto approvalRequestDto){

        log.info("Fetching vendor with: [id={}] in vendorApproval",vendorId);

        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(
                () -> {
                    log.error("Could not find vendor with vendor id: [id={}] in vendorApproval",vendorId);
                    return new VendorNotFoundException("Vendor not found to approve.");
                }
        );

        User user = userRepository.findById(approvalRequestDto.getApprovedBy()).orElseThrow(
                () -> {
                    log.error("User not found in vendor approval.");
                    throw new UserNotFoundException("User not found.");
                }
        );

        vendor.setApprovedBy(user);

        vendor.setApprovalStatus(approvalRequestDto.getApprovalStatus());

        vendor.setApprovedAt(LocalDateTime.now());

        Vendor updatedVendor = vendorRepository.save(vendor);

        return vendorMapper.mapToVendorResponse(updatedVendor);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.VENDOR_CACHE_NAME, key = "#vendorId")
    public boolean deleteVendor(Long vendorId) {
        log.info("Fetching vendor: [id={}] in deleteVendor",vendorId);

        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(
                () -> {
                    log.error("Could not find vendor with vendor id: [id={}] in deleteVendor",vendorId);
                    return new VendorNotFoundException("Vendor not found to delete.");
                }
        );

        vendor.setVendorProfileStatus(VendorProfileStatus.INACTIVE);

        vendorRepository.save(vendor);

        return true;
    }
}
