package com.bazaarhub.backend.feature.order.resources.request;

import com.bazaarhub.backend.shared.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusUpdateRequestDto implements Serializable {

    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;
}
