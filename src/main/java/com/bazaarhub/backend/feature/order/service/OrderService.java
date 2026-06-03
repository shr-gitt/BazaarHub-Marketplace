package com.bazaarhub.backend.feature.order.service;

import com.bazaarhub.backend.feature.order.resources.request.OrderRequestDto;
import com.bazaarhub.backend.feature.order.resources.request.OrderStatusUpdateRequestDto;
import com.bazaarhub.backend.feature.order.resources.response.OrderItemResponseDto;
import com.bazaarhub.backend.feature.order.resources.response.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderResponseDto placeOrder(Long userId, OrderRequestDto orderRequestDTO);

    OrderResponseDto getOrderById(Long userId, Long orderId);

    List<OrderResponseDto> getOrdersByUserId(Long userId);

    OrderItemResponseDto updateOrderStatus(Long orderId, OrderStatusUpdateRequestDto requestDTO);

    OrderResponseDto cancelOrder(Long userId, Long orderId);

    Page<OrderResponseDto> getAllOrdersForAdmin(Long adminId, Pageable pageable);

    Page<OrderItemResponseDto> getOrdersByVendorId(Long vendorId, Pageable pageable);
}
