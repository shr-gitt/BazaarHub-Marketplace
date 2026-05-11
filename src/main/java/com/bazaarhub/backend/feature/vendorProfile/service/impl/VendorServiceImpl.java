package com.bazaarhub.backend.feature.vendorProfile.service.impl;

import com.bazaarhub.backend.config.CacheConfig;
import com.bazaarhub.backend.feature.address.entity.Address;
import com.bazaarhub.backend.feature.address.repository.AddressRepository;
import com.bazaarhub.backend.feature.address.service.AddressService;
import com.bazaarhub.backend.feature.notification.enums.NotificationType;
import com.bazaarhub.backend.feature.notification.service.NotificationService;
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
import com.bazaarhub.backend.shared.enums.Role;
import com.bazaarhub.backend.shared.exception.UnauthorizedAccessException;
import com.bazaarhub.backend.shared.utils.InputUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
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
    private final AddressService addressService;
    private final AddressRepository addressRepository;
    private final NotificationService notificationService;

    @Override
    public Page<VendorResponseDto> getAllVendors(Pageable pageable) {
        log.info("Fetching all vendors.");
        return vendorRepository.findAll(pageable)
                .map(vendorMapper::mapToVendorResponse);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.VENDOR_CACHE_NAME, key = "#vendorId")
    public VendorResponseDto getVendorById(Long vendorId) {
        log.info("Fetching vendor with id={}", vendorId);
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(
                () -> {
                    log.error("Vendor fetch failed. Vendor not found with id={}", vendorId);
                    return new VendorNotFoundException("Vendor not found with given id.");
                }
        );

        if (vendor.getVendorProfileStatus() == VendorProfileStatus.ACTIVE)
            return vendorMapper.mapToVendorResponse(vendor);
        else {
            log.error("Vendor fetch failed. Vendor profile inactive. vendorId={}", vendorId);
            throw new VendorInactiveException("Vendor is not active.");
        }
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CacheConfig.VENDOR_CACHE_NAME, key = "#result.id")
    public VendorResponseDto createVendor(Long userId, VendorRequestDto vendorRequestDto) {
        log.info("Creating vendor profile for userId={}", userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.error("Vendor creation failed. User not found of id={}", userId);
                    return new UserNotFoundException("User not found with given id.");
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
                log.error("Vendor creation failed. Vendor information already exists of userId={}", userId);
                throw new EntityExistsException("Vendor account with this information already exists. Use other credentials.");
            }

            vendor = vendorMapper.mapToVendor(vendorRequestDto, existingVendor);
        }

        Address address = addressRepository.findByStreetAndMunicipality(
                vendorRequestDto.getAddressRequestDto().getStreet(),
                vendorRequestDto.getAddressRequestDto().getMunicipality()
        ).orElse(
                addressService.createAddress(vendorRequestDto.getAddressRequestDto())
        );

        vendor.setUser(user);
        vendor.setBusinessEmail(InputUtil.normalizeEmail(vendor.getBusinessEmail()));
        vendor.setAddress(address);
        vendor.setVendorProfileStatus(VendorProfileStatus.ACTIVE);
        vendor.setApprovalStatus(ApprovalStatus.PENDING);

        Vendor newVendor = vendorRepository.save(vendor);

        return vendorMapper.mapToVendorResponse(newVendor);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CacheConfig.VENDOR_CACHE_NAME, key = "#result.id")
    public VendorResponseDto updateVendor(Long userId, Long vendorId, VendorRequestDto vendorRequestDto) {

        log.info("Updating vendor of id={}", vendorId);

        Vendor toUpdateVendor = vendorRepository.findById(vendorId).orElseThrow(
                () -> {
                    log.error("Vendor update failed. Vendor not found with id={}", vendorId);
                    return new VendorNotFoundException("Vendor not found with given id.");
                }
        );

        if(!toUpdateVendor.getUser().getId().equals(userId)){
            log.error("Vendor update failed due to unauthorized access. userId={}, vendorId={}",
                    userId,
                    vendorId);
            throw new UnauthorizedAccessException("You are not authorized to update this vendor profile.");
        }

        Address address = addressRepository.findByStreetAndMunicipality(
                vendorRequestDto.getAddressRequestDto().getStreet(),
                vendorRequestDto.getAddressRequestDto().getMunicipality()
        ).orElse(
                addressService.createAddress(vendorRequestDto.getAddressRequestDto())
        );

        toUpdateVendor.setShopName(vendorRequestDto.getShopName());
        toUpdateVendor.setBusinessEmail(InputUtil.normalizeEmail(vendorRequestDto.getBusinessEmail()));
        toUpdateVendor.setBusinessPhone(vendorRequestDto.getBusinessPhone());
        toUpdateVendor.setAddress(address);

        Vendor updatedVendor = vendorRepository.save(toUpdateVendor);

        return vendorMapper.mapToVendorResponse(updatedVendor);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CacheConfig.VENDOR_CACHE_NAME, key = "#vendorId")
    public VendorResponseDto approveVendor(Long approverId, Long vendorId, ApprovalRequestDto approvalRequestDto) {

        log.info("Approving vendor with id={}", vendorId);

        User approver = userRepository.findById(approverId).orElseThrow(
                () -> {
                    log.error("Vendor approval failed. User not found.");
                    return new UserNotFoundException("User not found with given id.");
                }
        );

        if(Role.ADMIN != approver.getRole()){
            log.error("Vendor approval failed due to unauthorized access. approverId={}, vendorId={}",
                    approverId,
                    vendorId);
            throw new UnauthorizedAccessException("You are not authorized to approve this vendor profile.");
        }

        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(
                () -> {
                    log.error("Vendor approval failed. Vendor not found of vendorId={}", vendorId);
                    return new VendorNotFoundException("Vendor not found with given id.");
                }
        );

        vendor.setApprovedBy(approver);
        vendor.setApprovalStatus(approvalRequestDto.getApprovalStatus());
        vendor.setApprovedAt(LocalDateTime.now());

        Vendor updatedVendor = vendorRepository.save(vendor);

        if(ApprovalStatus.APPROVED == vendor.getApprovalStatus())
            notificationService.createNotification(
                    updatedVendor.getUser(),
                    "Vendor approved",
                    String.format(
                            "Your vendor account %s has been approved.",
                            updatedVendor.getShopName()
                    ),
                    NotificationType.VENDOR_APPROVED,
                    updatedVendor.getId()
            );
        else
            notificationService.createNotification(
                    updatedVendor.getUser(),
                    "Vendor rejected",
                    String.format(
                            "Your vendor account %s has been rejected.",
                            updatedVendor.getShopName()
                    ),
                    NotificationType.VENDOR_REJECTED,
                    updatedVendor.getId()
            );

        return vendorMapper.mapToVendorResponse(updatedVendor);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.VENDOR_CACHE_NAME, key = "#vendorId")
    public void deleteVendor(Long userId, Long vendorId) {
        log.info("Deleting vendor of vendorId={}", vendorId);

        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(
                () -> {
                    log.error("Vendor deletion failed. Vendor not found of vendorId={}", vendorId);
                    return new VendorNotFoundException("Vendor not found with given id.");
                }
        );

        if(!vendor.getUser().getId().equals(userId)){
            log.error("Vendor deletion failed due to unauthorized access. userId={}, vendorId={}",
                    userId,
                    vendorId);
            throw new UnauthorizedAccessException("You are not authorized to delete this vendor profile.");
        }

        vendor.setVendorProfileStatus(VendorProfileStatus.INACTIVE);

        vendorRepository.save(vendor);
    }
}
