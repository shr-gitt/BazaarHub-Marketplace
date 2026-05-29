package com.bazaarhub.backend.feature.product.service;

import com.bazaarhub.backend.feature.product.resource.request.ProductRequestDto;
import com.bazaarhub.backend.feature.product.resource.response.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ProductResponseDto createProduct(ProductRequestDto productRequestDto, MultipartFile file);

    ProductResponseDto getProductById(Long productId);

    Page<ProductResponseDto> getProductsByVendorId(Long vendorId, Pageable pageable);

    Page<ProductResponseDto> getAllProduct(Pageable pageable);

    ProductResponseDto updateProductById(Long productId, ProductRequestDto productRequestDto);

    void reserveStock(Long productId, Integer quantity);

    void restoreStock(Long productId, Integer quantity);

    void deleteProductById(Long productId);

    Page<ProductResponseDto> getRecommendedProducts(Pageable pageable);
}
