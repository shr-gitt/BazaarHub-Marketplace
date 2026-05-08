package com.bazaarhub.backend.feature.vendorProfile.resource.response;

import com.bazaarhub.backend.feature.address.resource.response.AddressResponseDto;
import com.bazaarhub.backend.feature.vendorProfile.enums.ApprovalStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class VendorResponseDto implements Serializable {
    private Long id;

    private Long userId;

    private String shopName;

    private String businessEmail;

    private String businessPhone;

    private String panCardNo;

    private String registrationNo;

    private AddressResponseDto address;

    private ApprovalStatus approvalStatus;

    private Long approvedBy;
}
