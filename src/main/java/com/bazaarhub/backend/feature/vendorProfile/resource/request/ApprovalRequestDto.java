package com.bazaarhub.backend.feature.vendorProfile.resource.request;

import com.bazaarhub.backend.feature.vendorProfile.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class ApprovalRequestDto implements Serializable {

    @NotNull(message = "Approval status is required.")
    private ApprovalStatus approvalStatus;
}
