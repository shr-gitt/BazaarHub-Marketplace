package com.bazaarhub.backend.feature.product.controller;

import com.bazaarhub.backend.feature.product.resource.request.ProductRequestDto;
import com.bazaarhub.backend.feature.product.resource.response.ProductResponseDto;
import com.bazaarhub.backend.feature.product.service.ProductService;
import com.bazaarhub.backend.shared.aop.LogExecutionTime;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/create-product")
    @LogExecutionTime
    public ApiResponseDto<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto productRequestDto) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Product created successfully.", productService.createProduct(productRequestDto));
    }

    @GetMapping("/product/{id}")
    @LogExecutionTime
    public ApiResponseDto<ProductResponseDto> getProductById(@PathVariable("id") Long productId) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Product fetched successfully.", productService.getProductById(productId));
    }

    @GetMapping("/products")
    @LogExecutionTime
    public ApiResponseDto<Page<ProductResponseDto>> getAllProducts(@PageableDefault(sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Products fetched successfully", productService.getAllProduct(pageable));
    }

    @PostMapping("/update-product/{id}")
    @LogExecutionTime
    public ApiResponseDto<ProductResponseDto> updateProductById(@PathVariable("id") Long productId, @Valid @RequestBody ProductRequestDto productRequestDto) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Product updated successfully.", productService.updateProductById(productId, productRequestDto));
    }

    @DeleteMapping("/product/{id}")
    @LogExecutionTime
    public ApiResponseDto<String> deleteProductById(@PathVariable("id") Long productId) {
        productService.deleteProductById(productId);
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Product deleted successfully");
    }
}
