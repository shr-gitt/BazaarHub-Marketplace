package com.bazaarhub.backend.feature.payment.scheduler;

import com.bazaarhub.backend.feature.payment.entity.Payment;
import com.bazaarhub.backend.feature.payment.repository.PaymentRepository;
import com.bazaarhub.backend.feature.payment.service.PaymentService;
import com.bazaarhub.backend.shared.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class PaymentExpiryScheduler {
    private PaymentService paymentService;
    private PaymentRepository paymentRepository;

    @Scheduled(fixedRate = 60000)
    private void expirePendingPayments(){
        List<Payment> expiredPayments = paymentRepository.findExpiredPendingPayments(
                PaymentStatus.PENDING,
                LocalDateTime.now()
        );

        for(Payment payment: expiredPayments){
            paymentService.markPaymentFailed(payment.getPid());
        }
    }
}
