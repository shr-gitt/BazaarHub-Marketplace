package com.bazaarhub.backend.feature.product.repository;

import com.bazaarhub.backend.feature.product.entity.Product;
import com.bazaarhub.backend.shared.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndStatusNot(Long id, ProductStatus status);
    Page<Product> findByStatusNot(ProductStatus status, Pageable pageable);
}
