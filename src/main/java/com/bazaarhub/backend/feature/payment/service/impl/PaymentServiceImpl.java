package com.bazaarhub.backend.feature.payment.service.impl;

import com.bazaarhub.backend.feature.cart.entity.Cart;
import com.bazaarhub.backend.feature.cart.exception.CartNotFoundException;
import com.bazaarhub.backend.feature.cart.repository.CartRepository;
import com.bazaarhub.backend.feature.notification.enums.NotificationType;
import com.bazaarhub.backend.feature.notification.service.NotificationService;
import com.bazaarhub.backend.feature.order.entity.Order;
import com.bazaarhub.backend.feature.order.entity.OrderItem;
import com.bazaarhub.backend.feature.order.repository.OrderRepository;
import com.bazaarhub.backend.feature.payment.entity.Payment;
import com.bazaarhub.backend.feature.payment.enums.PaymentType;
import com.bazaarhub.backend.feature.payment.exception.PaymentAmountMismatchException;
import com.bazaarhub.backend.feature.payment.exception.PaymentNotFoundException;
import com.bazaarhub.backend.feature.payment.exception.UnauthorizedPaymentAccessException;
import com.bazaarhub.backend.feature.payment.exception.WrongPaymentTypeException;
import com.bazaarhub.backend.feature.payment.util.EsewaSignatureUtil;
import com.bazaarhub.backend.feature.payment.mapper.PaymentMapper;
import com.bazaarhub.backend.feature.payment.repository.PaymentRepository;
import com.bazaarhub.backend.feature.payment.resource.request.PaymentRequestDto;
import com.bazaarhub.backend.feature.payment.resource.response.PaymentResponseDto;
import com.bazaarhub.backend.feature.payment.service.PaymentService;
import com.bazaarhub.backend.feature.points.service.PointsService;
import com.bazaarhub.backend.feature.product.service.ProductService;
import com.bazaarhub.backend.shared.enums.OrderPaymentStatus;
import com.bazaarhub.backend.shared.enums.OrderStatus;
import com.bazaarhub.backend.shared.enums.PaymentStatus;
import com.bazaarhub.backend.shared.exception.OrderNotFoundException;
import com.bazaarhub.backend.shared.exception.OrderPaidException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final EsewaSignatureUtil esewaSignatureUtil;
    private final RestTemplate restTemplate;
    private final PointsService pointsService;
    private final ProductService productService;
    private final NotificationService notificationService;

    @Value("${esewa.merchant.code}")
    private String merchantCode;

    @Value("${esewa.payment.url}")
    private String paymentUrl;

    @Value("${esewa.verify.url}")
    private String verifyUrl;

    @Value("${esewa.success.url}")
    private String successUrl;

    @Value("${esewa.failure.url}")
    private String failureUrl;

    @Value("${frontend.success.url}")
    private String frontendSuccessUrl;

    @Value("${frontend.failure.url}")
    private String frontendFailureUrl;

    @Override
    public Page<PaymentResponseDto> getAllPayments(Pageable pageable) {
        Pageable pages = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        return paymentRepository.findAll(pages)
                .map(paymentMapper::mapToPaymentResponse);
    }

    @Override
    public PaymentResponseDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Could not find payment with payment [id={}] in getPaymentById", id);
                    return new PaymentNotFoundException("Payment not found.");
                }
        );

        return paymentMapper.mapToPaymentResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto createPayment(Long id, PaymentRequestDto paymentRequestDto) {
        log.info("Creating payment. userId:{}", id);

        Order order = orderRepository.findById(paymentRequestDto.getOrderId())
                .orElseThrow(() -> {
                    log.error("Order not found. orderId{}:", paymentRequestDto.getOrderId());
                    return new OrderNotFoundException("Order not found");
                });

        if (OrderPaymentStatus.PAID == order.getPaymentStatus()) {
            throw new OrderPaidException("Order already paid");
        }

        Payment payment = new Payment();

        if (!order.getUser().getId().equals(id)) {
            throw new UnauthorizedPaymentAccessException("Unauthorized payment attempt");
        }

        payment.setUser(order.getUser());

        PaymentType paymentType = paymentRequestDto.getPaymentType();

        payment.setAmount(order.getTotalAmount());
        String pid = "ORD-" + order.getId() + "-" + System.currentTimeMillis();
        payment.setPid(pid);
        payment.setPaymentType(paymentType);
        if (PaymentType.WALLET.equals(paymentType)) {
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        }
        else{
            order.setPaymentStatus(OrderPaymentStatus.CASH_PENDING);
            payment.setPaymentStatus(PaymentStatus.CASH_PENDING);
            payment.setExpiresAt(null);
        }
        String dataToSign = "total_amount=" + order.getTotalAmount()
                + ",transaction_uuid=" + pid
                + ",product_code=" + merchantCode;

        Order updatedOrder = orderRepository.save(order);

        payment.setOrder(updatedOrder);
        Payment savedPayment = paymentRepository.save(payment);

        PaymentResponseDto response = paymentMapper.mapToPaymentResponse(savedPayment);

        if (PaymentType.WALLET.equals(paymentType)) {
            String signature = esewaSignatureUtil.generateSignature(dataToSign);

            response.setPaymentUrl(paymentUrl);
            response.setSuccessUrl(successUrl);
            response.setFailureUrl(failureUrl);
            response.setProductCode(merchantCode);
            response.setSignature(signature);
            response.setSignedFieldNames("total_amount,transaction_uuid,product_code");
        }
        else{
            Long userId = order.getUser().getId();

            Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> {
                log.error("Cart not found. userId : {}", userId);
                return new CartNotFoundException("Cart not found");
            });

            cart.getItems().clear();
            cartRepository.save(cart);
        }
        return response;
    }

    @Override
    @Transactional
    public PaymentResponseDto confirmCashPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->{
                    log.error("Payment not found. paymentId: {}", paymentId);
                    return new PaymentNotFoundException("Payment not found");
                });

        if (PaymentType.CASH_ON_DELIVERY != payment.getPaymentType()) {
            throw new WrongPaymentTypeException("Wrong payment type.");
        }

        if (PaymentStatus.SUCCESS == payment.getPaymentStatus()) {
            throw new OrderPaidException("Payment already confirmed");
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.getOrder().setPaymentStatus(OrderPaymentStatus.PAID);
        orderRepository.save(payment.getOrder());

        notificationService.createNotification(
                payment.getUser(),
                "Payment confirmed",
                "Your cash payment has been confirmed.",
                NotificationType.PAYMENT_SUCCESS,
                payment.getOrder().getId()
        );

        for (OrderItem orderItem : payment.getOrder().getOrderItems()) {
            notificationService.createNotification(
                    orderItem.getProduct().getVendor().getUser(),
                    "Payment received",
                    "Payment has been confirmed for product: " + orderItem.getProductName(),
                    NotificationType.PAYMENT_SUCCESS,
                    payment.getOrder().getId()
            );
        }

        return paymentMapper.mapToPaymentResponse(paymentRepository.save(payment));
    }

    private boolean verifyWithEsewa(Payment payment) {
        log.info("Verifying with eSewa, paymentId: {}", payment.getId());
        String url = UriComponentsBuilder.fromUriString(verifyUrl)
                .queryParam("product_code", merchantCode)
                .queryParam("total_amount", payment.getAmount())
                .queryParam("transaction_uuid", payment.getPid())
                .toUriString();

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map responseBody = response.getBody();

        if (responseBody == null) {
            log.error("eSewa verification failed: empty response");
            return false;
        }

        Object status = responseBody.get("status");

        if (!"COMPLETE".equals(status)) {
            log.warn("eSewa verification not complete. Status: {}", status);
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public String verifyPayment(String pid, String refId, String amt) {
        log.info("Verifying payment.");

        Payment payment = paymentRepository.findByPid(pid)
                .orElseThrow(() ->{
                    log.error("Payment not found. pid: {}", pid);
                    return new PaymentNotFoundException("Payment not found");
                });

        if (PaymentStatus.SUCCESS == payment.getPaymentStatus()) {
            if (!refId.equals(payment.getRefId())) {
                log.error("Replay attack suspected: different refId for completed payment [pid={}]", pid);
                return frontendFailureUrl;
            }

            log.error("Duplicate callback received for already-completed payment [pid={}]", pid);
            return frontendSuccessUrl;
        }

        BigDecimal requestAmount = new BigDecimal(amt.replace(",", "")).stripTrailingZeros();
        BigDecimal storedAmount = payment.getAmount().stripTrailingZeros();

        if (storedAmount.compareTo(requestAmount) != 0) {
            throw new PaymentAmountMismatchException("Amount mismatch for order: " + pid);
        }

        boolean verified = verifyWithEsewa(payment);

        Order order = payment.getOrder();

        if (verified) {
            pointsService.updateUserPoints(order.getUser(), order.getTotalAmount());
            order.setPaymentStatus(OrderPaymentStatus.PAID);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setRefId(refId);

            notificationService.createNotification(
                    order.getUser(),
                    "Payment successful",
                    "Your payment has been completed successfully.",
                    NotificationType.PAYMENT_SUCCESS,
                    order.getId()
            );

            for (OrderItem orderItem : order.getOrderItems()) {
                notificationService.createNotification(
                        orderItem.getProduct().getVendor().getUser(),
                        "Payment received",
                        "Payment received for product: " + orderItem.getProductName(),
                        NotificationType.PAYMENT_SUCCESS,
                        order.getId()
                );
            }

            Long userId = order.getUser().getId();

            Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> {
                log.error("Cart not found. userId : {}", userId);
                return new CartNotFoundException("Cart not found");
            });

            cart.getItems().clear();
            cartRepository.save(cart);
        } else {
            order.setPaymentStatus(OrderPaymentStatus.FAILED);
            payment.setPaymentStatus(PaymentStatus.FAILED);
            notificationService.createNotification(
                    order.getUser(),
                    "Payment failed",
                    "Your payment could not be verified.",
                    NotificationType.PAYMENT_FAILED,
                    order.getId()
            );

        }

        orderRepository.save(order);
        paymentRepository.save(payment);

        log.info(
                "Payment verified successfully. paymentId={}, orderId={}",
                payment.getId(),
                order.getId()
        );

        return verified ? frontendSuccessUrl
                : frontendFailureUrl;
    }

    @Override
    @Transactional
    public void markPaymentFailed(String pid) {
        log.info("Marking payment failed. pid: {}", pid);

        Payment payment = paymentRepository.findByPid(pid)
                .orElseThrow(() ->{
                    log.error(" Payment not found. pid: {}", pid);
                    return new PaymentNotFoundException("Payment not found");
                });

        Order order = payment.getOrder();
        if (payment.getPaymentStatus() == PaymentStatus.FAILED) {
            return;
        }

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        for (OrderItem item : order.getOrderItems()) {
            productService.restoreStock(
                    item.getProduct().getId(),
                    item.getQuantity()
            );
        }

        order.setPaymentStatus(OrderPaymentStatus.FAILED);
        order.setOrderStatus(OrderStatus.CANCELLED);
        payment.setPaymentStatus(PaymentStatus.FAILED);
        notificationService.createNotification(
                payment.getUser(),
                "Payment failed",
                "Your payment has failed.",
                NotificationType.PAYMENT_FAILED,
                payment.getOrder().getId()
        );
        paymentRepository.save(payment);
    }
}