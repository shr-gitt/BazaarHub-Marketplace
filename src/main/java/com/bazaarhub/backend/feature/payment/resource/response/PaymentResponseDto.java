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
    private String paymentUrl;
    private PaymentStatus paymentStatus;
    private PaymentType paymentType;
    private String refId;
    private String successUrl;
    private String failureUrl;
    private String merchantCode;
    private String signature;
    private String signedFieldNames;
}
