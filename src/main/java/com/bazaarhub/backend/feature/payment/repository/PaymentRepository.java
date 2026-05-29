package com.bazaarhub.backend.feature.payment.repository;

import com.bazaarhub.backend.feature.payment.entity.Payment;
import com.bazaarhub.backend.shared.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPid(String pid);
    @Query("""
        SELECT p FROM Payment p
        WHERE p.paymentStatus = :status
        AND p.expiresAt <= :now
    """)
    List<Payment> findExpiredPendingPayments(
            @Param("status") PaymentStatus status,
            @Param("now") LocalDateTime now
    );}
