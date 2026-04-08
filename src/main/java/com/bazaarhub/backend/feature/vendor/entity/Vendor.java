package com.bazaarhub.backend.feature.vendor.entity;

import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.vendor.enums.ApprovalStatus;
import com.bazaarhub.backend.feature.vendor.enums.VendorProfileStatus;
import com.bazaarhub.backend.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Vendor extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY ,optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @Column(name = "business_email", nullable = false, unique = true)
    private String businessEmail;

    @Column(name = "business_phone", nullable = false, unique = true)
    private String businessPhone;

    @Column(name = "pan_card_no", nullable = false, unique = true)
    private String panCardNo;

    @Column(name = "registration_no", nullable = false, unique = true)
    private String registrationNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "vendor_profile_status", nullable = false)
    private VendorProfileStatus vendorProfileStatus;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "country", nullable = false)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    private ApprovalStatus approvalStatus;

    // @Column(name = "approved_by") -> Mapping does not allow this
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id", nullable = true)
    private User approvedBy;

    @Column(name = "approved_at", nullable = true)
    private LocalDateTime approvedAt;
}
