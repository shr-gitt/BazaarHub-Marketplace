package com.bazaarhub.backend.feature.vendor.service.impl;

import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import com.bazaarhub.backend.feature.user.enums.Gender;
import com.bazaarhub.backend.feature.vendorProfile.entity.Vendor;
import com.bazaarhub.backend.feature.vendorProfile.enums.VendorProfileStatus;
import com.bazaarhub.backend.feature.vendorProfile.mapper.VendorMapper;
import com.bazaarhub.backend.feature.vendorProfile.repository.VendorRepository;
import com.bazaarhub.backend.feature.vendorProfile.resource.request.VendorRequestDto;
import com.bazaarhub.backend.feature.vendorProfile.resource.response.VendorResponseDto;
import com.bazaarhub.backend.feature.vendorProfile.service.impl.VendorServiceImpl;
import com.bazaarhub.backend.shared.enums.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void setUp() {
        vendorRequestDto = new VendorRequestDto("eSewa", "eSEwa@gmail.com", "9841234567","ae23Hj48","asdhf24", "Pulchowk", "Lalitpur", "Nepal");
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(vendorRepository, vendorMapper, userRepository);
    }

    @Test
    void createNewVendor() {
        ArgumentCaptor<Vendor> saveCaptor = ArgumentCaptor.forClass(Vendor.class);

        User user = new User();
        user.setId(12L);
        user.setFirstName("Mandip");
        user.setLastName("Shrestha");
        user.setEmail("shr12.mandip@gmail.com");
        user.setPhoneNumber("9887643526");
        user.setRole(Role.VENDOR);
        user.setGender(Gender.MALE);

        when(userRepository.findById(12L)).thenReturn(Optional.of(user));

        Vendor vendor = new Vendor();
        vendor.setId(1L);
        vendor.setUser(user);
        vendor.setShopName("eSewa");
        vendor.setBusinessEmail("eSEwa@gmail.com");
        vendor.setBusinessPhone("9841234567");

        when(vendorMapper.mapToVendor(vendorRequestDto)).thenReturn(vendor);

        when(vendorRepository.save(org.mockito.ArgumentMatchers.any(Vendor.class)))
                .thenAnswer(invocation -> {
                    Vendor v = invocation.getArgument(0);
                    v.setId(1L); // simulate DB assigning ID
                    return v;
                });

        VendorResponseDto vendorResponseDto = vendorService.createVendor(12L, vendorRequestDto);

        verify(vendorRepository).save(saveCaptor.capture());

        Vendor saved = saveCaptor.getValue();

        assertEquals("eSewa", saved.getShopName());
        assertEquals("esewa@gmail.com", saved.getBusinessEmail());
    }

    @Test
    void createExistingEmailVendor(){
        ArgumentCaptor<Vendor> saveCaptor = ArgumentCaptor.forClass(Vendor.class);

        User user = new User();
        user.setId(12L);
        user.setFirstName("Mandip");
        user.setLastName("Shrestha");
        user.setEmail("shr12.mandip@gmail.com");
        user.setPhoneNumber("9887643526");
        user.setRole(Role.VENDOR);
        user.setGender(Gender.MALE);

        Vendor vendor = new Vendor();
        vendor.setId(1L);
        vendor.setUser(user);
        vendor.setShopName("updatedeSewa");
        vendor.setBusinessEmail("esewa@gmail.com");
        vendor.setBusinessPhone("9841234579");

        VendorRequestDto existingVendorRequestDto = new VendorRequestDto(
                "updatedeSewa",
                "esewa@gmail.com",
                "9841234567",
                "ae23Hj48",
                "asdhf24",
                "Pulchowk",
                "Lalitpur",
                "Nepal");

        Vendor existingVendor = new Vendor();
        existingVendor.setId(1L);
        existingVendor.setUser(user);
        existingVendor.setShopName("eSewa");
        existingVendor.setBusinessEmail("esewa@gmail.com");
        existingVendor.setBusinessPhone("9841234567");
        existingVendor.setVendorProfileStatus(VendorProfileStatus.INACTIVE);

        when(userRepository.findById(12L)).thenReturn(Optional.of(user));

        when(vendorMapper.mapToVendor(existingVendorRequestDto)).thenReturn(vendor);

        when(vendorRepository.findByBusinessEmail(vendor.getBusinessEmail())).thenReturn(Optional.of(existingVendor));

        when(vendorMapper.mapToVendor(
                existingVendorRequestDto,
                existingVendor
        )).thenAnswer(invocation -> {
            existingVendor.setShopName(existingVendorRequestDto.getShopName());
            existingVendor.setBusinessEmail(existingVendorRequestDto.getBusinessEmail());
            existingVendor.setBusinessPhone(existingVendorRequestDto.getBusinessPhone());

            return existingVendor;
        });

        when(vendorRepository.save(org.mockito.ArgumentMatchers.any(Vendor.class)))
                .thenAnswer(invocation -> {
                    Vendor v = invocation.getArgument(0);
                    return v;
                });

        VendorResponseDto vendorResponseDto = vendorService.createVendor(12L, existingVendorRequestDto);

        verify(vendorRepository).save(saveCaptor.capture());

        Vendor saved = saveCaptor.getValue();

        assertEquals("updatedeSewa", saved.getShopName());
    }
}