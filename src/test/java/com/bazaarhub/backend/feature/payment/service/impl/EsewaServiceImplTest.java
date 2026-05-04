package com.bazaarhub.backend.feature.payment.service.impl;

import com.bazaarhub.backend.feature.payment.exception.EsewaVerificationException;
import com.bazaarhub.backend.feature.payment.service.PaymentService;
import com.bazaarhub.backend.feature.payment.util.EsewaSignatureUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EsewaServiceImplTest {
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private EsewaSignatureUtil esewaSignatureUtil;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private EsewaServiceImpl esewaService;

    private Map<String, String> callbackData;
    private String encodedData;

    @Value("${frontend.success.url}")
    private String frontendSuccessUrl;

    @Value("${frontend.failure.url}")
    private String frontendFailureUrl;

    @BeforeEach
    void setUp() throws Exception {
        callbackData = new HashMap<>();
        callbackData.put("transaction_uuid", "ORD-1-1714000000000");
        callbackData.put("transaction_code", "ABC123");
        callbackData.put("total_amount", "1000.0");
        callbackData.put("status", "COMPLETE");
        callbackData.put("signed_field_names", "transaction_uuid,transaction_code,total_amount,status");
        callbackData.put("signature", "validSignature");

        String json = new ObjectMapper().writeValueAsString(callbackData);
        encodedData = Base64.getEncoder().encodeToString(json.getBytes());

        when(objectMapper.readValue(anyString(), eq(Map.class))).thenReturn(callbackData);
    }

    @Test
    void processEsewaSuccess_shouldReturnTrueWhenSignatureValidAndPaymentVerified() {
        when(esewaSignatureUtil.verifyCallback(callbackData)).thenReturn(true);
        when(paymentService.verifyPayment("ORD-1-1714000000000", "ABC123", "1000.0")).thenReturn(frontendSuccessUrl);

        String result = esewaService.processEsewaSuccess(encodedData);

        assertEquals(frontendSuccessUrl, result);
        verify(paymentService).verifyPayment("ORD-1-1714000000000", "ABC123", "1000.0");
    }

    @Test
    void processEsewaSuccess_shouldReturnFalseWhenPaymentVerificationFails() {
        when(esewaSignatureUtil.verifyCallback(callbackData)).thenReturn(true);
        when(paymentService.verifyPayment(any(), any(), any())).thenReturn(frontendFailureUrl);

        String result = esewaService.processEsewaSuccess(encodedData);

        assertEquals(frontendFailureUrl, result);
    }

    @Test
    void processEsewaFailure_shouldMarkPaymentFailedWhenSignatureValid() {
        when(esewaSignatureUtil.verifyCallback(callbackData)).thenReturn(true);

        esewaService.processEsewaFailure(encodedData);

        verify(paymentService).markPaymentFailed("ORD-1-1714000000000");
    }

    @Test
    void processEsewaFailure_shouldNotMarkFailedWhenSignatureInvalid() {
        when(esewaSignatureUtil.verifyCallback(callbackData)).thenReturn(false);

        esewaService.processEsewaFailure(encodedData);

        verifyNoInteractions(paymentService);
    }
}