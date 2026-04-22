package com.bazaarhub.backend.feature.order.repository;

import com.bazaarhub.backend.feature.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
