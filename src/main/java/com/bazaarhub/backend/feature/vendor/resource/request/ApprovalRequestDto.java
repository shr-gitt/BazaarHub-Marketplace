package com.bazaarhub.backend.feature.vendor.resource.request;

import com.bazaarhub.backend.feature.vendor.enums.ApprovalStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class ApprovalRequestDto implements Serializable {

    @NotBlank(message = "Vendor Id is required.")
    private Long vendorId;

    @NotBlank(message = "Approval status is required.")
    private ApprovalStatus approvalStatus;
}
