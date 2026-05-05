package com.bazaarhub.backend.feature.payment.repository;

import com.bazaarhub.backend.feature.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPid(String pid);
}
