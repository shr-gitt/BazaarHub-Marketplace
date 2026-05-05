package com.bazaarhub.backend.feature.payment.resource.request;

import com.bazaarhub.backend.feature.payment.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class PaymentRequestDto implements Serializable {
    @NotNull(message = "Order ID is required.")
    private Long orderId;

    @NotNull(message = "Payment type is required.")
    private PaymentType paymentType;
}
