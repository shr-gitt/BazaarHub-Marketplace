package com.bazaarhub.backend.feature.vendorProfile.mapper;

import com.bazaarhub.backend.feature.address.mapper.AddressMapper;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.vendorProfile.resource.request.VendorRequestDto;
import com.bazaarhub.backend.feature.vendorProfile.resource.response.VendorResponseDto;
import com.bazaarhub.backend.feature.vendorProfile.entity.Vendor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VendorMapper {

    private final AddressMapper addressMapper;

    public Vendor mapToVendor(VendorRequestDto vendorRequestDto){
        Vendor vendor = new Vendor();

        vendor.setShopName(vendorRequestDto.getShopName());

        vendor.setBusinessEmail(vendorRequestDto.getBusinessEmail());

        vendor.setBusinessPhone(vendorRequestDto.getBusinessPhone());

        vendor.setPanCardNo(vendorRequestDto.getPanCardNo());

        vendor.setRegistrationNo(vendorRequestDto.getRegistrationNo());

        return vendor;
    }

    public Vendor mapToVendor(VendorRequestDto vendorRequestDto, Vendor vendor){

        vendor.setShopName(vendorRequestDto.getShopName());

        vendor.setBusinessEmail(vendorRequestDto.getBusinessEmail());

        vendor.setBusinessPhone(vendorRequestDto.getBusinessPhone());

        vendor.setPanCardNo(vendorRequestDto.getPanCardNo());

        vendor.setRegistrationNo(vendorRequestDto.getRegistrationNo());

        return vendor;
    }

    public VendorResponseDto mapToVendorResponse(Vendor vendor){
        VendorResponseDto vendorResponseDto = new VendorResponseDto();

        vendorResponseDto.setId(vendor.getId());

        User user = vendor.getUser();

        vendorResponseDto.setUserId(user.getId());

        vendorResponseDto.setShopName(vendor.getShopName());

        vendorResponseDto.setBusinessEmail(vendor.getBusinessEmail());

        vendorResponseDto.setBusinessPhone(vendor.getBusinessPhone());

        vendorResponseDto.setPanCardNo(vendor.getPanCardNo());

        vendorResponseDto.setRegistrationNo(vendor.getRegistrationNo());

        vendorResponseDto.setAddress(addressMapper.mapToAddressResponseDto(vendor.getAddress()));

        vendorResponseDto.setApprovalStatus(vendor.getApprovalStatus());

        User approver = vendor.getApprovedBy();

        vendorResponseDto.setApprovedBy(
                approver != null ? approver.getId() : null
        );

        return vendorResponseDto;
    }
}
