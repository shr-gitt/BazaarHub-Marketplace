package com.bazaarhub.backend.feature.vendor.resource.response;

import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.vendor.enums.ApprovalStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;

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

    private String address;

    private String city;

    private String country;

    private ApprovalStatus approvalStatus;

    private Long approvedBy;
}
