package com.bazaarhub.backend.feature.product.repository;

import com.bazaarhub.backend.feature.product.entity.Product;
import com.bazaarhub.backend.shared.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndStatusNot(Long id, ProductStatus status);
    Page<Product> findByVendor_IdAndStatusNot(Long vendorId, ProductStatus status, Pageable pageable);
    Page<Product> findByStatusNot(ProductStatus status, Pageable pageable);

    //For recommendation logic
    Page<Product> findByCategory_IdInAndStatusNot(
            List<Long> categoryIds,
            ProductStatus status,
            Pageable pageable
    );

    @Modifying
    @Query("""
        UPDATE Product p
        SET p.stockQuantity = p.stockQuantity - :quantity
        WHERE p.id = :productId
          AND p.stockQuantity >= :quantity
          AND p.status <> 'DELETED'
    """)
    int reserveStock(
            Long productId,
            Integer quantity
    );

    @Modifying
    @Query("""
        UPDATE Product p
        SET p.stockQuantity = p.stockQuantity + :quantity
        WHERE p.id = :productId
          AND p.status <> 'DELETED'
    """)
    int restoreStock(
            Long productId,
            Integer quantity
    );

    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusNot(
            String name, String description, ProductStatus status, Pageable pageable);
}
