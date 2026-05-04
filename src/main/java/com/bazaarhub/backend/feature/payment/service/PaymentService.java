package com.bazaarhub.backend.feature.payment.service;

import com.bazaarhub.backend.feature.payment.resource.request.PaymentRequestDto;
import com.bazaarhub.backend.feature.payment.resource.response.PaymentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    Page<PaymentResponseDto> getAllPayments(Pageable pageable);

    PaymentResponseDto getPaymentById(Long id);

    PaymentResponseDto createPayment(Long id, PaymentRequestDto paymentRequestDto);

    PaymentResponseDto confirmCashPayment(Long paymentId);

    String verifyPayment(String pid, String refId, String amt);

    void markPaymentFailed(String pid);
}
