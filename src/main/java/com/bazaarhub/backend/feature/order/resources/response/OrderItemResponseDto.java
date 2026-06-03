package com.bazaarhub.backend.feature.order.resources.response;

import com.bazaarhub.backend.shared.enums.OrderPaymentStatus;
import com.bazaarhub.backend.shared.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDto implements Serializable {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private OrderPaymentStatus paymentStatus;
    private String shippingAddress;
    private String contactNumber;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
