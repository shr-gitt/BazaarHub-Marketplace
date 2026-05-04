package com.bazaarhub.backend.feature.vendorProfile.service.impl;

import com.bazaarhub.backend.config.CacheConfig;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import com.bazaarhub.backend.feature.vendorProfile.resource.request.ApprovalRequestDto;
import com.bazaarhub.backend.feature.vendorProfile.resource.request.VendorRequestDto;
import com.bazaarhub.backend.feature.vendorProfile.resource.response.VendorResponseDto;
import com.bazaarhub.backend.feature.vendorProfile.entity.Vendor;
import com.bazaarhub.backend.feature.vendorProfile.enums.ApprovalStatus;
import com.bazaarhub.backend.feature.vendorProfile.enums.VendorProfileStatus;
import com.bazaarhub.backend.feature.vendorProfile.exception.VendorInactiveException;
import com.bazaarhub.backend.feature.vendorProfile.exception.VendorNotFoundException;
import com.bazaarhub.backend.feature.vendorProfile.mapper.VendorMapper;
import com.bazaarhub.backend.feature.vendorProfile.repository.VendorRepository;
import com.bazaarhub.backend.feature.vendorProfile.service.VendorService;
import com.bazaarhub.backend.shared.utils.InputUtil;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public VendorResponseDto getVendorById(Long vendorId) {
        log.info("Fetching vendor with id={}", vendorId);
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(
                () -> {
                    log.error("Vendor not found of id={}", vendorId);
                    return new VendorNotFoundException("Vendor not found.");
                }
        );

        if (vendor.getVendorProfileStatus() == VendorProfileStatus.ACTIVE)
            return vendorMapper.mapToVendorResponse(vendor);
        else {
            log.error("Vendor is not active of id={}", vendorId);
            throw new VendorInactiveException("Vendor is not active.");
        }
    }

    @Override
    @CachePut(cacheNames = CacheConfig.VENDOR_CACHE_NAME, key = "#result.id")
    public VendorResponseDto createVendor(Long userId, VendorRequestDto vendorRequestDto) {
        log.info("Creating vendor of userId={}", userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.error("User not found of id={}", userId);
                    return new UserNotFoundException("User not found.");
                }
        );

        Vendor vendor = vendorMapper.mapToVendor(vendorRequestDto);

        Optional<Vendor> existingVendorOpt = vendorRepository.findByBusinessEmail(vendor.getBusinessEmail());

        if (existingVendorOpt.isEmpty())
            existingVendorOpt = vendorRepository.findByBusinessPhone(vendor.getBusinessPhone());

        if (existingVendorOpt.isEmpty())
            existingVendorOpt = vendorRepository.findByPanCardNo(vendor.getPanCardNo());

        if (existingVendorOpt.isEmpty())
            existingVendorOpt = vendorRepository.findByRegistrationNo(vendor.getRegistrationNo());

        if (existingVendorOpt.isPresent()) {
            Vendor existingVendor = existingVendorOpt.get();

            if (VendorProfileStatus.ACTIVE == existingVendor.getVendorProfileStatus()) {
                log.error("Vendor information already exists of userId={}", userId);
                throw new EntityExistsException("Vendor account with this information already exists. Use other credentials.");
            }

            vendor = vendorMapper.mapToVendor(vendorRequestDto, existingVendor);
        }

        vendor.setUser(user);
        vendor.setBusinessEmail(InputUtil.normalizeEmail(vendor.getBusinessEmail()));
        vendor.setVendorProfileStatus(VendorProfileStatus.ACTIVE);
        vendor.setApprovalStatus(ApprovalStatus.PENDING);

        Vendor newVendor = vendorRepository.save(vendor);

        return vendorMapper.mapToVendorResponse(newVendor);
    }

    @Override
    @CachePut(cacheNames = CacheConfig.VENDOR_CACHE_NAME, key = "#result.id")
    public VendorResponseDto updateVendor(Long userId, VendorRequestDto vendorRequestDto) {

        log.info("Fetching vendor of userId={}", userId);

        Vendor toUpdateVendor = vendorRepository.findByUserId(userId).orElseThrow(
                () -> {
                    log.error("Vendor not found of userId={}", userId);
                    return new VendorNotFoundException("Vendor not found to update.");
                }
        );

        Vendor vendor = vendorMapper.mapToVendor(vendorRequestDto);

        toUpdateVendor.setShopName(vendor.getShopName());
        toUpdateVendor.setBusinessEmail(InputUtil.normalizeEmail(vendor.getBusinessEmail()));
        toUpdateVendor.setBusinessPhone(vendor.getBusinessPhone());
        toUpdateVendor.setAddress(vendor.getAddress());
        toUpdateVendor.setCity(vendor.getCity());
        toUpdateVendor.setCountry(vendor.getCountry());

        Vendor updatedVendor = vendorRepository.save(toUpdateVendor);

        return vendorMapper.mapToVendorResponse(updatedVendor);
    }

    @Override
    @CachePut(cacheNames = CacheConfig.VENDOR_CACHE_NAME, key = "#vendorId")
    public VendorResponseDto approveVendor(Long approverId, Long vendorId, ApprovalRequestDto approvalRequestDto) {

        log.info("Fetching vendor with id={}", vendorId);

        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(
                () -> {
                    log.error("Vendor not found during approval of vendorId={}", vendorId);
                    return new VendorNotFoundException("Vendor not found to approve.");
                }
        );

        User user = userRepository.findById(approverId).orElseThrow(
                () -> {
                    log.error("User not found in vendor approval.");
                    return new UserNotFoundException("User not found.");
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
    public void deleteVendor(Long vendorId) {
        log.info("Fetching vendor of vendorId={}", vendorId);

        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(
                () -> {
                    log.error("Vendor not found of vendorId={}", vendorId);
                    return new VendorNotFoundException("Vendor not found to delete.");
                }
        );

        vendor.setVendorProfileStatus(VendorProfileStatus.INACTIVE);

        vendorRepository.save(vendor);
    }
}
