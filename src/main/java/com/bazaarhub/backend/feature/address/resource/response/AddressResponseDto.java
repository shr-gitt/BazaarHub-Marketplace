package com.bazaarhub.backend.feature.address.resource.response;

import com.bazaarhub.backend.shared.enums.District;
import com.bazaarhub.backend.shared.enums.Municipality;
import com.bazaarhub.backend.shared.enums.Province;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDto implements Serializable {

    private Long id;
    private Province province;
    private District district;
    private Municipality municipality;
    private Integer wardNo;
    private String street;
    private String postalCode;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}