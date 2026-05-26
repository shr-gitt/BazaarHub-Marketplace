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
        return product;
    }

    public ProductResponseDto mapToProductResponse(Product product) {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(product.getId());
        productResponseDto.setName(product.getName());
        productResponseDto.setDescription(product.getDescription());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setDiscountPrice(product.getDiscountPrice());
        productResponseDto.setStockQuantity(product.getStockQuantity());
        productResponseDto.setCategoryId(product.getCategory().getId());
        productResponseDto.setCategory(product.getCategory().getName());
        productResponseDto.setStatus(product.getStatus());
        productResponseDto.setVendorName(product.getVendor().getShopName());
        productResponseDto.setCreatedAt(product.getCreatedAt());
        productResponseDto.setModifiedAt(product.getModifiedAt());

        return productResponseDto;
    }
}
