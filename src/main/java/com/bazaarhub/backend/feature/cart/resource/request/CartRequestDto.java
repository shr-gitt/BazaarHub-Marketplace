package com.bazaarhub.backend.feature.cart.resource.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartRequestDto implements Serializable {
    private Long userId;
    private List<CartItemRequestDto> items = new ArrayList<>();
}
