package com.bazaarhub.backend.feature.payment.service.impl;

import com.bazaarhub.backend.feature.notification.service.NotificationService;
import com.bazaarhub.backend.feature.order.entity.Order;
import com.bazaarhub.backend.feature.order.repository.OrderRepository;
import com.bazaarhub.backend.feature.payment.entity.Payment;
import com.bazaarhub.backend.feature.payment.enums.PaymentType;
import com.bazaarhub.backend.feature.payment.exception.PaymentAmountMismatchException;
import com.bazaarhub.backend.feature.payment.mapper.PaymentMapper;
import com.bazaarhub.backend.feature.payment.repository.PaymentRepository;
import com.bazaarhub.backend.feature.payment.resource.request.PaymentRequestDto;
import com.bazaarhub.backend.feature.payment.resource.response.PaymentResponseDto;
import com.bazaarhub.backend.feature.points.service.PointsService;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.shared.enums.OrderPaymentStatus;
import com.bazaarhub.backend.shared.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PointsService pointsService;

    @Mock
    private NotificationService notificationService;


    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order order;
    private User user;
    private Payment payment;

    private String frontendSuccessUrl;
    private String frontendFailureUrl;

    @BeforeEach
    void setUp() {

        frontendSuccessUrl = "http://localhost:5173/payment/success";
        frontendFailureUrl = "http://localhost:5173/payment/failure";

        ReflectionTestUtils.setField(paymentService, "merchantCode", "EPAYTEST");
        ReflectionTestUtils.setField(paymentService, "paymentUrl", "https://rc-epay.esewa.com.np/api/epay/main/v2/form");
        ReflectionTestUtils.setField(paymentService, "verifyUrl", "https://rc-epay.esewa.com.np/api/epay/transaction/status/");
        ReflectionTestUtils.setField(paymentService, "successUrl", "http://localhost:8080/api/payment/esewa/success");
        ReflectionTestUtils.setField(paymentService, "failureUrl", "http://localhost:8080/api/payment/esewa/failure");

        ReflectionTestUtils.setField(paymentService, "frontendSuccessUrl", frontendSuccessUrl);
        ReflectionTestUtils.setField(paymentService, "frontendFailureUrl", frontendFailureUrl);

        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        order = new Order();
        ReflectionTestUtils.setField(order, "id", 1L);
        order.setUser(user);
        order.setTotalAmount(new BigDecimal("1000.00"));
        order.setPaymentStatus(OrderPaymentStatus.PENDING);

        payment = new Payment();
        ReflectionTestUtils.setField(payment, "id", 1L);
        payment.setPid("ORD-1-1714000000000");
        payment.setAmount(new BigDecimal("1000.00"));
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setOrder(order);
        payment.setUser(user);
    }

    @Test
    void createPayment_shouldSucceedForValidRequest() {
        PaymentRequestDto dto = new PaymentRequestDto(1L, PaymentType.CASH_ON_DELIVERY);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any())).thenReturn(payment);
        when(paymentMapper.mapToPaymentResponse(any())).thenReturn(new PaymentResponseDto());

        PaymentResponseDto result = paymentService.createPayment(1L, dto);

        assertNotNull(result);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void createPayment_shouldThrowWhenUserDoesNotOwnOrder() {
        PaymentRequestDto dto = new PaymentRequestDto(1L, PaymentType.CASH_ON_DELIVERY);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // user id 99 does not own the order (owned by user id 1)
        assertThrows(Exception.class,
                () -> paymentService.createPayment(99L, dto));
    }

    @Test
    void verifyPayment_shouldReturnTrueWhenEsewaConfirms() {
        when(paymentRepository.findByPid("ORD-1-1714000000000")).thenReturn(Optional.of(payment));
        Map<String, Object> esewaResponse = Map.of("status", "COMPLETE");
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(esewaResponse));
        when(orderRepository.save(any())).thenReturn(order);
        when(paymentRepository.save(any())).thenReturn(payment);

        String result = paymentService.verifyPayment("ORD-1-1714000000000", "REF123", "1000.0");

        assertEquals(frontendSuccessUrl, result);
        assertEquals(OrderPaymentStatus.PAID, order.getPaymentStatus());
        assertEquals(PaymentStatus.SUCCESS, payment.getPaymentStatus());
        assertEquals("REF123", payment.getRefId());
    }

    @Test
    void verifyPayment_shouldReturnFalseWhenEsewaDoesNotConfirm() {
        when(paymentRepository.findByPid("ORD-1-1714000000000")).thenReturn(Optional.of(payment));
        Map<String, Object> esewaResponse = Map.of("status", "FAILED");
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(esewaResponse));
        when(orderRepository.save(any())).thenReturn(order);
        when(paymentRepository.save(any())).thenReturn(payment);

        String result = paymentService.verifyPayment("ORD-1-1714000000000", "REF123", "1000.0");

        assertEquals(frontendFailureUrl, result);
        assertEquals(OrderPaymentStatus.FAILED, order.getPaymentStatus());
        assertEquals(PaymentStatus.FAILED, payment.getPaymentStatus());

        verify(pointsService, never()).updateUserPoints(any(), any());
        verify(notificationService, times(1))
                .createNotification(any(User.class), anyString(), anyString(), any(), anyLong());

    }

    @Test
    void verifyPayment_shouldThrowOnAmountMismatch() {
        when(paymentRepository.findByPid("ORD-1-1714000000000")).thenReturn(Optional.of(payment));

        assertThrows(PaymentAmountMismatchException.class,
                () -> paymentService.verifyPayment("ORD-1-1714000000000", "REF123", "500.0"));
    }

    @Test
    void verifyPayment_shouldReturnTrueImmediatelyIfAlreadySuccess() {
        Payment newPayment = new Payment();
        newPayment.setPid("ORD-1-1714000000001");
        newPayment.setRefId("REF123");
        newPayment.setPaymentStatus(PaymentStatus.SUCCESS);
        when(paymentRepository.findByPid("ORD-1-1714000000001")).thenReturn(Optional.of(newPayment));

        String result = paymentService.verifyPayment("ORD-1-1714000000001", "REF123", "1000.0");

        assertEquals(frontendSuccessUrl, result);
        verifyNoInteractions(restTemplate); // eSewa API not called
    }

    @Test
    void markPaymentFailed_shouldSetStatusToFailed() {
        when(paymentRepository.findByPid("ORD-1-1714000000000")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        paymentService.markPaymentFailed("ORD-1-1714000000000");

        assertEquals(PaymentStatus.FAILED, payment.getPaymentStatus());
        assertEquals(OrderPaymentStatus.FAILED, order.getPaymentStatus());
        verify(notificationService, times(1))
                .createNotification(any(User.class), anyString(), anyString(), any(), anyLong());

        verify(paymentRepository, times(1)).save(payment);
    }
}