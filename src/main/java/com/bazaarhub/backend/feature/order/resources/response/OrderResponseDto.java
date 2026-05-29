package com.bazaarhub.backend.feature.order.resources.response;

import com.bazaarhub.backend.shared.enums.OrderPaymentStatus;
import com.bazaarhub.backend.shared.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto implements Serializable {
    private Long id;
    private Long userId;
    private List<OrderItemResponseDto> items = new ArrayList<>();
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private OrderPaymentStatus paymentStatus;
    private String shippingAddress;
    private String contactNumber;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
