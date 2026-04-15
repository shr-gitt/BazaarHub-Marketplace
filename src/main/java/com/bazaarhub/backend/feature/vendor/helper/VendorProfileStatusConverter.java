package com.bazaarhub.backend.feature.vendor.helper;

import com.bazaarhub.backend.feature.vendor.enums.VendorProfileStatus;
import com.bazaarhub.backend.shared.helper.EnumConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VendorProfileStatusConverter extends EnumConverter {
    VendorProfileStatusConverter(){
        super(VendorProfileStatus.class);
    }
}
