package com.bazaarhub.backend.feature.product.mapper;

import com.bazaarhub.backend.feature.product.entity.Product;
import com.bazaarhub.backend.feature.product.resource.request.ProductRequestDto;
import com.bazaarhub.backend.feature.product.resource.response.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    public Product mapToProduct(ProductRequestDto request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        return product;
    }

    public ProductResponseDto mapToProductResponse(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscountPrice(),
                product.getStockQuantity(),
                product.getImageUrl(),
                product.getCategory().getName(),
                product.getStatus(),
                product.getVendor().getShopName(),
                product.getCreatedAt(),
                product.getModifiedAt()

        );

    }
}
