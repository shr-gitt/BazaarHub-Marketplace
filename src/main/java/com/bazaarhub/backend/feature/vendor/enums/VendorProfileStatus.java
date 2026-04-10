package com.bazaarhub.backend.feature.vendor.enums;

import com.bazaarhub.backend.shared.enums.EnumWithId;

public enum VendorProfileStatus implements EnumWithId {
    ACTIVE("0"),
    INACTIVE("1");

    private final String value;

    VendorProfileStatus(String value){
        this.value = value;
    }

    public String getId(){
        return value;
    }
}
