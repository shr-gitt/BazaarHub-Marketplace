package com.bazaarhub.backend.feature.vendor.helper;

import com.bazaarhub.backend.feature.vendor.enums.ApprovalStatus;
import com.bazaarhub.backend.shared.helper.EnumConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ApprovalStatusConverter extends EnumConverter {
    ApprovalStatusConverter(){
        super(ApprovalStatus.class);
    }
}
