package com.bazaarhub.backend.feature.product.service;

import com.bazaarhub.backend.feature.product.resource.request.ProductRequestDto;
import com.bazaarhub.backend.feature.product.resource.response.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponseDto createProduct(ProductRequestDto productRequest);

    ProductResponseDto getProductById(Long productId);

    Page<ProductResponseDto> getAllProduct(Pageable pageable);

    ProductResponseDto updateProductById(Long productId, ProductRequestDto productRequestDto);

    void deleteProductById(Long productId);
}
