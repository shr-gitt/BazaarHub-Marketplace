package com.bazaarhub.backend.feature.vendor.enums;

import com.bazaarhub.backend.shared.enums.EnumWithId;

public enum ApprovalStatus implements EnumWithId {
    APPROVED("0"),
    PENDING("1"),
    REJECTED("2");

    private final String value;

    ApprovalStatus(String value){
        this.value = value;
    }

    public String getId(){
        return value;
    }
}
