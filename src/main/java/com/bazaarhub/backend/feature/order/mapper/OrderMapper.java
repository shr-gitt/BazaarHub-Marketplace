package com.bazaarhub.backend.feature.order.mapper;

import com.bazaarhub.backend.feature.order.entity.Order;
import com.bazaarhub.backend.feature.order.entity.OrderItem;
import com.bazaarhub.backend.feature.order.resources.response.OrderItemResponseDto;
import com.bazaarhub.backend.feature.order.resources.response.OrderResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderItemResponseDto mapToOrderItemResponseDTO(OrderItem orderItem) {
        return new OrderItemResponseDto(orderItem.getId(), orderItem.getProduct().getId(), orderItem.getProductName(), orderItem.getQuantity(), orderItem.getPricePerUnit(), orderItem.getTotalPrice(), orderItem.getOrderStatus(), orderItem.getOrder().getPaymentStatus(), orderItem.getOrder().getShippingAddress(), orderItem.getOrder().getContactNumber(), orderItem.getOrder().getRemarks(), orderItem.getOrder().getCreatedAt(), orderItem.getOrder().getModifiedAt());
    }

    public OrderResponseDto mapToOrderResponseDTO(Order order) {
        List<OrderItemResponseDto> items = order.getOrderItems().stream().map(this::mapToOrderItemResponseDTO).toList();
        return new OrderResponseDto(order.getId(), order.getUser().getId(), items, order.getTotalAmount(), order.getOrderStatus(), order.getPaymentStatus(), order.getShippingAddress(), order.getContactNumber(), order.getRemarks(), order.getCreatedAt(), order.getModifiedAt());

    }
}
