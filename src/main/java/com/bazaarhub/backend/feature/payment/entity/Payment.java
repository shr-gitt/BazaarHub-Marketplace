package com.bazaarhub.backend.feature.payment.entity;

import com.bazaarhub.backend.feature.order.entity.Order;
import com.bazaarhub.backend.feature.payment.enums.PaymentType;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.shared.entity.BaseEntity;
import com.bazaarhub.backend.shared.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
public class Payment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(unique = true, nullable = false)
    private String pid;    // unique payment identifier ->transaction_uuid
    private String refId;  // returned after verification

    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}
