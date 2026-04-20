package com.bazaarhub.backend.feature.cart.service;

import com.bazaarhub.backend.feature.cart.resource.request.CartItemRequestDto;
import com.bazaarhub.backend.feature.cart.resource.response.CartResponseDto;

public interface CartService {
    CartResponseDto addToCart(Long userId, CartItemRequestDto cartItemRequestDto);

    CartResponseDto updateCartItem(Long userId, Long productId, Integer quantity);

    CartResponseDto getCartByUserId(Long userId);

    CartResponseDto removeItemFromCart(Long userId, Long productId);

    void clearCart(Long userId);
}
