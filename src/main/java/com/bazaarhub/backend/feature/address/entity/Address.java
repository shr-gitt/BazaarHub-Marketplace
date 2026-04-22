package com.bazaarhub.backend.feature.address.entity;

import com.bazaarhub.backend.shared.entity.BaseEntity;
import com.bazaarhub.backend.shared.enums.District;
import com.bazaarhub.backend.shared.enums.Municipality;
import com.bazaarhub.backend.shared.enums.Province;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "province", length = 50)
    private Province province;

    @Enumerated(EnumType.STRING)
    @Column(name = "district", nullable = false, length = 100)
    private District district;

    @Enumerated(EnumType.STRING)
    @Column(name = "municipality", nullable = false, length = 100)
    private Municipality municipality;

    @Column(name = "ward_no", nullable = false)
    private Integer wardNo;

    @Column(name = "street", length = 255, nullable = false)
    private String street;

    @Column(name = "postal_code", length = 10, nullable = false)
    private String postalCode;
}