package com.bazaarhub.backend.feature.payment.resource.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CashPaymentConfirmDto {
    @NotNull(message = "Payment Id is required.")
    private Long paymentId;
}
