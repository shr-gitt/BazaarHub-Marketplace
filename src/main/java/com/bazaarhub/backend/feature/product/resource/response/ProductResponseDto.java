package com.bazaarhub.backend.feature.product.resource.response;

import com.bazaarhub.backend.shared.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductResponseDto implements Serializable {
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private Integer stockQuantity;

    private String imageUrl;

    private String category;

    private ProductStatus status;

    private String vendorName;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
