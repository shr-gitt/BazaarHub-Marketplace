package com.bazaarhub.backend.feature.cart.resource.response;

import com.bazaarhub.backend.shared.enums.CartItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CartItemResponseDto implements Serializable {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal totalPrice;
    private CartItemStatus status;
}
