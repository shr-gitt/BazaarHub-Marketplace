package com.bazaarhub.backend.feature.payment.mapper;

import com.bazaarhub.backend.feature.payment.entity.Payment;
import com.bazaarhub.backend.feature.payment.resource.response.PaymentResponseDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentMapper {
    public PaymentResponseDto mapToPaymentResponse(Payment payment){
        PaymentResponseDto response = new PaymentResponseDto();
        response.setId(payment.getId());
        response.setTransaction_uuid(payment.getPid());
        response.setAmount(payment.getAmount());
        response.setTaxAmount(BigDecimal.ZERO);
        response.setServiceCharge(BigDecimal.ZERO);
        response.setDeliveryCharge(BigDecimal.ZERO);
        response.setTotalAmount(payment.getAmount());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setPaymentType(payment.getPaymentType());
        response.setRefId(payment.getRefId());

        return response;
    }
}
