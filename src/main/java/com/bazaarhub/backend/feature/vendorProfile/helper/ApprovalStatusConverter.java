package com.bazaarhub.backend.feature.vendorProfile.helper;

import com.bazaarhub.backend.feature.vendorProfile.enums.ApprovalStatus;
import com.bazaarhub.backend.shared.helper.EnumConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ApprovalStatusConverter extends EnumConverter {
    ApprovalStatusConverter(){
        super(ApprovalStatus.class);
    }
}
