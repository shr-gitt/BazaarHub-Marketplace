package com.bazaarhub.backend.feature.vendor.service.impl;

import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import com.bazaarhub.backend.feature.user.enums.Gender;
import com.bazaarhub.backend.feature.vendorProfile.entity.Vendor;
import com.bazaarhub.backend.feature.vendorProfile.enums.ApprovalStatus;
import com.bazaarhub.backend.feature.vendorProfile.enums.VendorProfileStatus;
import com.bazaarhub.backend.feature.vendorProfile.exception.VendorInactiveException;
import com.bazaarhub.backend.feature.vendorProfile.exception.VendorNotFoundException;
import com.bazaarhub.backend.feature.vendorProfile.mapper.VendorMapper;
import com.bazaarhub.backend.feature.vendorProfile.repository.VendorRepository;
import com.bazaarhub.backend.feature.vendorProfile.resource.request.ApprovalRequestDto;
import com.bazaarhub.backend.feature.vendorProfile.resource.request.VendorRequestDto;
import com.bazaarhub.backend.feature.vendorProfile.resource.response.VendorResponseDto;
import com.bazaarhub.backend.feature.vendorProfile.service.impl.VendorServiceImpl;
import com.bazaarhub.backend.shared.enums.Role;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class VendorServiceImplTest {

    @Mock
    private VendorMapper vendorMapper;

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VendorServiceImpl vendorService;

    private VendorRequestDto vendorRequestDto;

    private User user;

    private User approver;

    private Vendor vendor;

    private VendorResponseDto vendorResponseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(12L);
        user.setFirstName("Mandip");
        user.setLastName("Shrestha");
        user.setEmail("shr12.mandip@gmail.com");
        user.setPhoneNumber("9887643526");
        user.setRole(Role.VENDOR);
        user.setGender(Gender.MALE);

        approver = new User();
        approver.setId(99L);
        approver.setRole(Role.ADMIN);

        vendorRequestDto = new VendorRequestDto("eSewa", "eSEwa@gmail.com", "9841234567", "ae23Hj48", "asdhf24", "Pulchowk", "Lalitpur", "Nepal");

        vendor = new Vendor();
        vendor.setId(1L);
        vendor.setUser(user);
        vendor.setShopName("eSewa");
        vendor.setBusinessEmail("eSEwa@gmail.com");
        vendor.setBusinessPhone("9841234567");
        vendor.setPanCardNo("ae23Hj48");
        vendor.setRegistrationNo("asdhf24");
        vendor.setAddress("Pulchowk");
        vendor.setCity("Lalitpur");
        vendor.setCountry("Nepal");
        vendor.setVendorProfileStatus(VendorProfileStatus.ACTIVE);
        vendor.setApprovalStatus(ApprovalStatus.PENDING);

        vendorResponseDto = new VendorResponseDto();
        vendorResponseDto.setId(1L);
        vendorResponseDto.setShopName("eSewa");
        vendorResponseDto.setBusinessEmail("esewa@gmail.com");
        vendorResponseDto.setBusinessPhone("9841234567");
        vendorResponseDto.setApprovalStatus(ApprovalStatus.PENDING);
    }


    @Test
    void createNewVendor_shouldCreateNewVendor_whenNoDuplicateExists() {
        ArgumentCaptor<Vendor> saveCaptor = ArgumentCaptor.forClass(Vendor.class);
        when(userRepository.findById(12L)).thenReturn(Optional.of(user));
        when(vendorMapper.mapToVendor(vendorRequestDto)).thenReturn(vendor);

        when(vendorRepository.findByBusinessEmail("eSEwa@gmail.com")).thenReturn(Optional.empty());
        when(vendorRepository.findByBusinessPhone("9841234567")).thenReturn(Optional.empty());
        when(vendorRepository.findByPanCardNo("ae23Hj48")).thenReturn(Optional.empty());
        when(vendorRepository.findByRegistrationNo("asdhf24")).thenReturn(Optional.empty());

        when(vendorRepository.save(any(Vendor.class))).thenReturn(vendor);
        when(vendorMapper.mapToVendorResponse(vendor)).thenReturn(vendorResponseDto);

        VendorResponseDto result = vendorService.createVendor(12L, vendorRequestDto);

        assertNotNull(result);
        assertEquals("eSewa", result.getShopName());

        verify(vendorRepository).save(saveCaptor.capture());

        Vendor saved = saveCaptor.getValue();

        assertEquals(user, saved.getUser());

        assertEquals("esewa@gmail.com", saved.getBusinessEmail());

        assertEquals(VendorProfileStatus.ACTIVE, saved.getVendorProfileStatus());
        assertEquals(ApprovalStatus.PENDING, saved.getApprovalStatus());

    }

    @Test
    void createVendor_shouldReuseInactiveExistingVendor_whenEmailAlreadyExistsButInactive() {
        ArgumentCaptor<Vendor> saveCaptor = ArgumentCaptor.forClass(Vendor.class);

        Vendor existingVendor = new Vendor();
        existingVendor.setId(1L);
        existingVendor.setUser(user);
        existingVendor.setShopName("Old eSewa");
        existingVendor.setBusinessEmail("esewa@gmail.com");
        existingVendor.setBusinessPhone("9841234567");
        existingVendor.setVendorProfileStatus(VendorProfileStatus.INACTIVE);

        when(userRepository.findById(12L)).thenReturn(Optional.of(user));
        when(vendorMapper.mapToVendor(vendorRequestDto)).thenReturn(vendor);
        when(vendorRepository.findByBusinessEmail("eSEwa@gmail.com")).thenReturn(Optional.of(existingVendor));
        when(vendorMapper.mapToVendor(vendorRequestDto, existingVendor)).thenReturn(existingVendor);

        when(vendorRepository.save(any(Vendor.class))).thenReturn(existingVendor);
        when(vendorMapper.mapToVendorResponse(existingVendor)).thenReturn(vendorResponseDto);

        VendorResponseDto result = vendorService.createVendor(12L, vendorRequestDto);

        assertNotNull(result);

        verify(vendorRepository).save(saveCaptor.capture());

        Vendor saved = saveCaptor.getValue();

        assertEquals(user, saved.getUser());
        assertEquals(VendorProfileStatus.ACTIVE, saved.getVendorProfileStatus());
        assertEquals(ApprovalStatus.PENDING, saved.getApprovalStatus());
    }

    @Test
    void createVendor_shouldThrowException_whenActiveVendorAlreadyExists() {
        Vendor existingVendor = new Vendor();
        existingVendor.setId(1L);
        existingVendor.setVendorProfileStatus(VendorProfileStatus.ACTIVE);

        when(userRepository.findById(12L)).thenReturn(Optional.of(user));
        when(vendorMapper.mapToVendor(vendorRequestDto)).thenReturn(vendor);
        when(vendorRepository.findByBusinessEmail("eSEwa@gmail.com")).thenReturn(Optional.of(existingVendor));

        assertThrows(
                EntityExistsException.class,
                () -> vendorService.createVendor(12L, vendorRequestDto)
        );

        verify(vendorRepository, never()).save(any(Vendor.class));
    }

    @Test
    void createVendor_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(12L)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> vendorService.createVendor(12L, vendorRequestDto)
        );

        verify(vendorRepository, never()).save(any(Vendor.class));
    }

    @Test
    void getVendorById_shouldReturnVendor_whenActive() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(vendorMapper.mapToVendorResponse(vendor)).thenReturn(vendorResponseDto);

        VendorResponseDto result = vendorService.getVendorById(1L);

        assertNotNull(result);
        assertEquals("eSewa", result.getShopName());

        verify(vendorRepository, times(1)).findById(1L);
        verify(vendorMapper, times(1)).mapToVendorResponse(vendor);
    }

    @Test
    void getVendorById_shouldThrowException_whenVendorNotFound() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                VendorNotFoundException.class,
                () -> vendorService.getVendorById(1L)
        );

        verify(vendorMapper, never()).mapToVendorResponse(any(Vendor.class));
    }

    @Test
    void getVendorById_shouldThrowException_whenVendorInactive() {
        vendor.setVendorProfileStatus(VendorProfileStatus.INACTIVE);

        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));

        assertThrows(
                VendorInactiveException.class,
                () -> vendorService.getVendorById(1L)
        );

        verify(vendorMapper, never()).mapToVendorResponse(any(Vendor.class));
    }

    @Test
    void getAllVendors_shouldReturnPagedVendors() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("shopName").ascending());
        Page<Vendor> vendorPage = new PageImpl<>(List.of(vendor), pageable, 1);

        when(vendorRepository.findAll(any(Pageable.class))).thenReturn(vendorPage);
        when(vendorMapper.mapToVendorResponse(vendor)).thenReturn(vendorResponseDto);

        Page<VendorResponseDto> result = vendorService.getAllVendors(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("eSewa", result.getContent().get(0).getShopName());

        verify(vendorRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void updateVendor_shouldUpdateVendor_whenFound() {
        Vendor updatedInput = new Vendor();
        updatedInput.setShopName("Updated eSewa");
        updatedInput.setBusinessEmail("Updated@Email.com");
        updatedInput.setBusinessPhone("9800000000");
        updatedInput.setAddress("New Address");
        updatedInput.setCity("Kathmandu");
        updatedInput.setCountry("Nepal");

        VendorResponseDto updatedResponse = new VendorResponseDto();
        updatedResponse.setId(1L);
        updatedResponse.setShopName("Updated eSewa");
        updatedResponse.setBusinessEmail("updated@email.com");

        when(vendorRepository.findByUserId(12L)).thenReturn(Optional.of(vendor));
        when(vendorMapper.mapToVendor(vendorRequestDto)).thenReturn(updatedInput);
        when(vendorRepository.save(vendor)).thenReturn(vendor);
        when(vendorMapper.mapToVendorResponse(vendor)).thenReturn(updatedResponse);

        VendorResponseDto result = vendorService.updateVendor(12L, vendorRequestDto);

        assertNotNull(result);
        assertEquals("Updated eSewa", vendor.getShopName());
        assertEquals("updated@email.com", vendor.getBusinessEmail());

        assertEquals("9800000000", vendor.getBusinessPhone());

        verify(vendorRepository, times(1)).findByUserId(12L);
        verify(vendorRepository, times(1)).save(vendor);
    }

    @Test
    void updateVendor_shouldThrowException_whenVendorNotFound() {
        when(vendorRepository.findByUserId(12L)).thenReturn(Optional.empty());

        assertThrows(
                VendorNotFoundException.class,
                () -> vendorService.updateVendor(12L, vendorRequestDto)
        );

        verify(vendorRepository, never()).save(any(Vendor.class));
    }

    @Test
    void approveVendor_shouldThrowException_whenVendorNotFound() {
        ApprovalRequestDto approvalRequestDto = new ApprovalRequestDto();

        when(vendorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                VendorNotFoundException.class,
                () -> vendorService.approveVendor(99L, 1L, approvalRequestDto)
        );

        verify(vendorRepository, never()).save(any(Vendor.class));
    }

    @Test
    void approveVendor_shouldThrowException_whenApproverNotFound() {
        ApprovalRequestDto approvalRequestDto = new ApprovalRequestDto();
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> vendorService.approveVendor(99L, 1L, approvalRequestDto)
        );

        verify(vendorRepository, never()).save(any(Vendor.class));
    }

    @Test
    void deleteVendor_shouldMarkVendorInactive_whenFound() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(vendorRepository.save(vendor)).thenReturn(vendor);

        vendorService.deleteVendor(1L);

        assertEquals(VendorProfileStatus.INACTIVE, vendor.getVendorProfileStatus());

        verify(vendorRepository, times(1)).findById(1L);
        verify(vendorRepository, times(1)).save(vendor);
    }

    @Test
    void deleteVendor_shouldThrowException_whenVendorNotFound() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                VendorNotFoundException.class,
                () -> vendorService.deleteVendor(1L)
        );

        verify(vendorRepository, never()).save(any(Vendor.class));
    }
}