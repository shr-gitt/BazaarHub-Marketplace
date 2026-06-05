package com.bazaarhub.backend.feature.product.service;

import com.bazaarhub.backend.feature.product.entity.Product;
import com.bazaarhub.backend.feature.product.resource.response.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductSearchService {
    void indexProduct(Product product);

    void removeProduct(Long productId);

    Page<ProductResponseDto> search(String keyword, Pageable pageable);
}
