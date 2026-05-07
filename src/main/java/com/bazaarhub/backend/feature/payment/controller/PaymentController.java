package com.bazaarhub.backend.feature.payment.controller;

import com.bazaarhub.backend.feature.payment.resource.request.CashPaymentConfirmDto;
import com.bazaarhub.backend.feature.payment.resource.request.PaymentRequestDto;
import com.bazaarhub.backend.feature.payment.resource.response.PaymentResponseDto;
import com.bazaarhub.backend.feature.payment.service.PaymentService;
import com.bazaarhub.backend.shared.aop.LogExecutionTime;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import com.bazaarhub.backend.shared.utils.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/payments")
    @LogExecutionTime
    public ApiResponseDto<Page<PaymentResponseDto>> getAllPayments(@PageableDefault(sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable){
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "All Payments fetched", paymentService.getAllPayments(pageable));
    }

    @GetMapping("/payment/{id}")
    @LogExecutionTime
    public ApiResponseDto<PaymentResponseDto> getPaymentById(@PathVariable Long id){
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor fetched.", paymentService.getPaymentById(id));
    }

    @PostMapping("/payment/create")
    @LogExecutionTime
    public ApiResponseDto<PaymentResponseDto> createPayment(@Valid @RequestBody PaymentRequestDto paymentRequestDto){
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Payment created.", paymentService.createPayment(AuthUtil.getCurrentUserId(), paymentRequestDto));
    }

    @PostMapping("/payment/cash/confirm")
    @LogExecutionTime
    public ApiResponseDto<PaymentResponseDto> confirmCashPayment(
            @Valid @RequestBody CashPaymentConfirmDto dto) {
        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "Cash payment confirmed.",
                paymentService.confirmCashPayment(dto.getPaymentId())
        );
    }
}
