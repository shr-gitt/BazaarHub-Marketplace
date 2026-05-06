package com.bazaarhub.backend.feature.payment.service.impl;

import com.bazaarhub.backend.feature.order.entity.Order;
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
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderRepository orderRepository;
    private final EsewaSignatureUtil esewaSignatureUtil;
    private final RestTemplate restTemplate;
    private final PointsService pointsService;

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
        log.info("Fetching all payments.");

        Pageable pages = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        return paymentRepository.findAll(pages)
                .map(paymentMapper::mapToPaymentResponse);
    }

    @Override
    public PaymentResponseDto getPaymentById(Long id) {
        log.info("Getting payment by id {}",id);

        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Could not find payment with payment [id={}] in getPaymentById",id);
                    return new PaymentNotFoundException("Payment not found.");
                }
        );

        return paymentMapper.mapToPaymentResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto createPayment(Long id, PaymentRequestDto paymentRequestDto) {
        log.info("Creating payment.");

        Order order = orderRepository.findById(paymentRequestDto.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (PaymentStatus.PAID == order.getPaymentStatus()) {
            throw new OrderPaidException("Order already paid");
        }

        Payment payment = new Payment();

        if (!order.getUser().getId().equals(id)) {
            throw new UnauthorizedPaymentAccessException("Unauthorized payment attempt");
        }

        payment.setOrder(order);
        payment.setUser(order.getUser());

        PaymentType paymentType = paymentRequestDto.getPaymentType();

        payment.setAmount(order.getTotalAmount());
        String pid = "ORD-" + order.getId() + "-" + System.currentTimeMillis();
        payment.setPid(pid);
        payment.setPaymentType(paymentType);
        payment.setPaymentStatus(PaymentStatus.PENDING);

        String dataToSign = "total_amount=" + order.getTotalAmount()
                + ",transaction_uuid=" + pid
                + ",product_code=" + merchantCode;

        Payment savedPayment = paymentRepository.save(payment);

        PaymentResponseDto response = paymentMapper.mapToPaymentResponse(savedPayment);

        if (paymentType.equals(PaymentType.WALLET)) {
            String signature = esewaSignatureUtil.generateSignature(dataToSign);

            response.setPaymentUrl(paymentUrl);
            response.setSuccessUrl(successUrl);
            response.setFailureUrl(failureUrl);
            response.setMerchantCode(merchantCode);
            response.setSignature(signature);
            response.setSignedFieldNames("total_amount,transaction_uuid,product_code");
        }
        return response;
    }

    @Override
    @Transactional
    public PaymentResponseDto confirmCashPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        if(PaymentType.CASH_ON_DELIVERY != payment.getPaymentType()){
            throw new WrongPaymentTypeException("Wrong payment type.");
        }

        if (PaymentStatus.SUCCESS == payment.getPaymentStatus()) {
            throw new OrderPaidException("Payment already confirmed");
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.getOrder().setPaymentStatus(PaymentStatus.PAID);
        orderRepository.save(payment.getOrder());

        return paymentMapper.mapToPaymentResponse(paymentRepository.save(payment));
    }

    private boolean verifyWithEsewa(Payment payment) {
        log.info("Verifying with eSewa");
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
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        if (PaymentStatus.SUCCESS == payment.getPaymentStatus()) {
            if (!refId.equals(payment.getRefId())) {
                log.error("Replay attack suspected: different refId for completed payment [pid={}]", pid);
                return frontendFailureUrl;
            }

            log.warn("Duplicate callback received for already-completed payment [pid={}]", pid);
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
            order.setPaymentStatus(PaymentStatus.PAID);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setRefId(refId);
        } else {
            order.setPaymentStatus(PaymentStatus.FAILED);
            payment.setPaymentStatus(PaymentStatus.FAILED);
        }

        orderRepository.save(order);
        paymentRepository.save(payment);

        return verified ? frontendSuccessUrl
                : frontendFailureUrl;
    }

    @Override
    @Transactional
    public void markPaymentFailed(String pid) {
        log.info("Marking payment failed.");

        Payment payment = paymentRepository.findByPid(pid)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        if (payment.getOrder() != null) {
            payment.getOrder().setPaymentStatus(PaymentStatus.FAILED);
        }
        payment.setPaymentStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }
}