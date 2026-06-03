package com.bazaarhub.backend.feature.order.repository;

import com.bazaarhub.backend.feature.order.entity.OrderItem;
import com.bazaarhub.backend.shared.enums.OrderPaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Page<OrderItem> findByProductVendorIdAndOrderPaymentStatusIn(
            Long vendorId,
            List<OrderPaymentStatus> statuses,
            Pageable pageable
    );}
