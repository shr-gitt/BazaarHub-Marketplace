package com.bazaarhub.backend.feature.vendor.resource.request;

import com.bazaarhub.backend.feature.vendor.enums.ApprovalStatus;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class ApprovalRequestDto implements Serializable {
    private Long approvedBy;

    private ApprovalStatus approvalStatus;
}
