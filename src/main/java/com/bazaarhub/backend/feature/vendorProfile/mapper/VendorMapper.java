package com.bazaarhub.backend.feature.vendorProfile.mapper;

import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.vendorProfile.resource.request.VendorRequestDto;
import com.bazaarhub.backend.feature.vendorProfile.resource.response.VendorResponseDto;
import com.bazaarhub.backend.feature.vendorProfile.entity.Vendor;
import org.springframework.stereotype.Component;

@Component
public class VendorMapper {

    public Vendor mapToVendor(VendorRequestDto vendorRequestDto){
        Vendor vendor = new Vendor();

        vendor.setShopName(vendorRequestDto.getShopName());

        vendor.setBusinessEmail(vendorRequestDto.getBusinessEmail());

        vendor.setBusinessPhone(vendorRequestDto.getBusinessPhone());

        vendor.setPanCardNo(vendorRequestDto.getPanCardNo());

        vendor.setRegistrationNo(vendorRequestDto.getRegistrationNo());

        vendor.setAddress(vendorRequestDto.getAddress());

        vendor.setCity(vendorRequestDto.getCity());

        vendor.setCountry(vendorRequestDto.getCountry());

        return vendor;
    }

    public Vendor mapToVendor(VendorRequestDto vendorRequestDto, Vendor vendor){

        vendor.setShopName(vendorRequestDto.getShopName());

        vendor.setBusinessEmail(vendorRequestDto.getBusinessEmail());

        vendor.setBusinessPhone(vendorRequestDto.getBusinessPhone());

        vendor.setPanCardNo(vendorRequestDto.getPanCardNo());

        vendor.setRegistrationNo(vendorRequestDto.getRegistrationNo());

        vendor.setAddress(vendorRequestDto.getAddress());

        vendor.setCity(vendorRequestDto.getCity());

        vendor.setCountry(vendorRequestDto.getCountry());

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

        vendorResponseDto.setAddress(vendor.getAddress());

        vendorResponseDto.setCity(vendor.getCity());

        vendorResponseDto.setCountry(vendor.getCountry());

        vendorResponseDto.setApprovalStatus(vendor.getApprovalStatus());

        User approver = vendor.getApprovedBy();

        vendorResponseDto.setApprovedBy(
                approver != null ? approver.getId() : null
        );

        return vendorResponseDto;
    }
}
