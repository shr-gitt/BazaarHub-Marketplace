package com.bazaarhub.backend.feature.payment.resource.response;

import com.bazaarhub.backend.feature.payment.enums.PaymentType;
import com.bazaarhub.backend.shared.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentResponseDto {
    private Long id;
    private String transaction_uuid;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal serviceCharge;
    private BigDecimal deliveryCharge;
    private String productCode;
    private String paymentUrl;
    private PaymentStatus paymentStatus;
    private PaymentType paymentType;
    private String refId;
    private String successUrl;
    private String failureUrl;
    private String signature;
    private String signedFieldNames;
}
