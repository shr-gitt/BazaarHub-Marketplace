package com.bazaarhub.backend.feature.cart.controller;

import com.bazaarhub.backend.feature.cart.resource.request.CartItemRequestDto;
import com.bazaarhub.backend.feature.cart.resource.request.UpdateCartItemRequestDto;
import com.bazaarhub.backend.feature.cart.resource.response.CartResponseDto;
import com.bazaarhub.backend.feature.cart.service.CartService;
import com.bazaarhub.backend.shared.aop.LogExecutionTime;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import com.bazaarhub.backend.shared.utils.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/items")
    @LogExecutionTime
    public ApiResponseDto<CartResponseDto> addToCart(@Valid @RequestBody CartItemRequestDto cartItemRequestDto) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Item added to cart successfully.",
                cartService.addToCart(AuthUtil.getCurrentUserId(), cartItemRequestDto));
    }

    @GetMapping
    @LogExecutionTime
    public ApiResponseDto<CartResponseDto> getCart() {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Cart fetched successfully", cartService.getCartByUserId(AuthUtil.getCurrentUserId()));
    }

    @PostMapping("/items/{productId}")
    @LogExecutionTime
    public ApiResponseDto<CartResponseDto> updateCartItem(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequestDto cartItemRequestDto
    ) {
        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "Cart item updated successfully.",
                cartService.updateCartItem(
                        AuthUtil.getCurrentUserId(),
                        productId,
                        cartItemRequestDto.getQuantity()
                )
        );
    }

    @DeleteMapping("/items/{productId}")
    @LogExecutionTime
    public ApiResponseDto<CartResponseDto> removeFromCart(@PathVariable Long productId) {
        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "Item removed from cart successfully.",
                cartService.removeItemFromCart(AuthUtil.getCurrentUserId(), productId)
        );
    }

    @DeleteMapping
    @LogExecutionTime
    public ApiResponseDto<Void> clearCart() {
        cartService.clearCart(AuthUtil.getCurrentUserId());
        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "Cart cleared successfully.",
                null
        );
    }
}
