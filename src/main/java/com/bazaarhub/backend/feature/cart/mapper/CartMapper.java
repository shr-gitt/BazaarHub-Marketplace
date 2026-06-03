package com.bazaarhub.backend.feature.cart.mapper;

import com.bazaarhub.backend.feature.cart.entity.Cart;
import com.bazaarhub.backend.feature.cart.entity.CartItem;
import com.bazaarhub.backend.feature.cart.resource.response.CartItemResponseDto;
import com.bazaarhub.backend.feature.cart.resource.response.CartResponseDto;
import com.bazaarhub.backend.shared.enums.CartItemStatus;
import com.bazaarhub.backend.shared.service.MinioService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@AllArgsConstructor
public class CartMapper {

    private final MinioService minioService;

    public CartItemResponseDto mapToCartItemResponse(CartItem cartItem, CartItemStatus cartItemStatus) {
        return new CartItemResponseDto(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                minioService.getImageUrl(cartItem.getProduct().getImageUrl()),
                cartItem.getQuantity(),
                cartItem.getPricePerUnit(),
                cartItem.getTotalPrice(),
                cartItemStatus
        );
    }

    public CartResponseDto mapToCartResponse(Cart cart, List<CartItemResponseDto> items) {
        BigDecimal totalPrice = items
                .stream()
                .map(CartItemResponseDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponseDto(
                cart.getId(),
                cart.getUser().getId(),
                items,
                totalPrice,
                cart.getCreatedAt(),
                cart.getModifiedAt()
        );
    }
}
