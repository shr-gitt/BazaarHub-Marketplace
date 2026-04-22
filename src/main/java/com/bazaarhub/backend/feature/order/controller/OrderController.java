package com.bazaarhub.backend.feature.order.controller;

import com.bazaarhub.backend.feature.order.resources.request.OrderRequestDto;
import com.bazaarhub.backend.feature.order.resources.request.OrderStatusUpdateRequestDto;
import com.bazaarhub.backend.feature.order.resources.response.OrderResponseDto;
import com.bazaarhub.backend.feature.order.service.OrderService;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import com.bazaarhub.backend.shared.utils.AuthUtil;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final AuthUtil authUtil;

    @PostMapping("/checkout")
    public ApiResponseDto<OrderResponseDto> placeOrder(@Valid @RequestBody OrderRequestDto requestDto) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Order placed successfully.", orderService.placeOrder(authUtil.getCurrentUserId(), requestDto));
    }

    @GetMapping("/{orderId}")
    public ApiResponseDto<OrderResponseDto> getOrderById(@PathVariable Long orderId) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Order fetched successfully.", orderService.getOrderById(authUtil.getCurrentUserId(), orderId));
    }

    @GetMapping
    public ApiResponseDto<List<OrderResponseDto>> getOrdersByUserId() {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Orders fetched successfully.", orderService.getOrdersByUserId(authUtil.getCurrentUserId()));
    }

    @PatchMapping("/{orderId}/status")
    public ApiResponseDto<OrderResponseDto> updateOrderStatus(@PathVariable Long orderId, @Valid @RequestBody OrderStatusUpdateRequestDto requestDto) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Order status updated successfully.", orderService.updateOrderStatus(orderId, requestDto));
    }

    @PatchMapping("/{orderId}/cancel")
    public ApiResponseDto<OrderResponseDto> cancelOrder(@PathVariable Long orderId) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Order cancelled successfully.", orderService.cancelOrder(authUtil.getCurrentUserId(), orderId));
    }
}
