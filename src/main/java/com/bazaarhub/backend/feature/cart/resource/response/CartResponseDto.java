package com.bazaarhub.backend.feature.cart.resource.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class CartResponseDto implements Serializable {
    private Long id;
    private Long userId;
    private List<CartItemResponseDto> items = new ArrayList<>();
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
