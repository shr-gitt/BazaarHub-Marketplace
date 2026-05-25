package com.bazaarhub.backend.feature.order.repository;

import com.bazaarhub.backend.feature.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
}




