package com.bazaarhub.backend.feature.order.controller;

import com.bazaarhub.backend.feature.order.resources.request.OrderRequestDto;
import com.bazaarhub.backend.feature.order.resources.request.OrderStatusUpdateRequestDto;
import com.bazaarhub.backend.feature.order.resources.response.OrderItemResponseDto;
import com.bazaarhub.backend.feature.order.resources.response.OrderResponseDto;
import com.bazaarhub.backend.feature.order.service.OrderService;
import com.bazaarhub.backend.shared.aop.LogExecutionTime;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import com.bazaarhub.backend.shared.utils.AuthUtil;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/checkout")
    public ApiResponseDto<OrderResponseDto> placeOrder(@Valid @RequestBody OrderRequestDto requestDto) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Order placed successfully.", orderService.placeOrder(AuthUtil.getCurrentUserId(), requestDto));
    }

    @GetMapping("/{orderId}")
    public ApiResponseDto<OrderResponseDto> getOrderById(@PathVariable Long orderId) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Order fetched successfully.", orderService.getOrderById(AuthUtil.getCurrentUserId(), orderId));
    }

    @GetMapping
    public ApiResponseDto<List<OrderResponseDto>> getOrdersByUserId() {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Orders fetched successfully.", orderService.getOrdersByUserId(AuthUtil.getCurrentUserId()));
    }

    @PatchMapping("/{orderId}/status")
    public ApiResponseDto<OrderItemResponseDto> updateOrderStatus(@PathVariable Long orderId, @Valid @RequestBody OrderStatusUpdateRequestDto requestDto) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Order status updated successfully.", orderService.updateOrderStatus(orderId, requestDto));
    }

    @PatchMapping("/{orderId}/cancel")
    public ApiResponseDto<OrderResponseDto> cancelOrder(@PathVariable Long orderId) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Order cancelled successfully.", orderService.cancelOrder(AuthUtil.getCurrentUserId(), orderId));
    }

    @GetMapping("/admin")
    public ApiResponseDto<Page<OrderResponseDto>> getAllOrdersForAdmin(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "All orders fetched successfully.",
                orderService.getAllOrdersForAdmin(AuthUtil.getCurrentUserId(), pageable)
        );
    }

    @GetMapping("/vendor/{vendorId}")
    @LogExecutionTime
    public ApiResponseDto<Page<OrderItemResponseDto>> getOrdersByVendorId(@PathVariable("vendorId") Long vendorId, @PageableDefault(sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable){
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor products fetched successfully", orderService.getOrdersByVendorId(vendorId, pageable));
    }
}
