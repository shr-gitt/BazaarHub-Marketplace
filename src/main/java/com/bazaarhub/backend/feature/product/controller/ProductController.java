package com.bazaarhub.backend.feature.product.controller;

import com.bazaarhub.backend.feature.product.resource.request.ProductRequestDto;
import com.bazaarhub.backend.feature.product.resource.response.ProductResponseDto;
import com.bazaarhub.backend.feature.product.service.ProductSearchService;
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
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductSearchService productSearchService;

    @PostMapping(value = "/create-product",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @LogExecutionTime
    @PreAuthorize("@vendorAuth.isApproved(#productRequestDto.getVendorId())")
    public ApiResponseDto<ProductResponseDto> createProduct(@Valid
                                                            @RequestPart("productRequestDto") ProductRequestDto productRequestDto,
                                                            @RequestPart("file") MultipartFile file) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Product created successfully.", productService.createProduct(productRequestDto, file));
    }

    @GetMapping("/product/{id}")
    @LogExecutionTime
    public ApiResponseDto<ProductResponseDto> getProductById(@PathVariable("id") Long productId) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Product fetched successfully.", productService.getProductById(productId));
    }

    @GetMapping("/vendor-product/{vendorId}")
    @LogExecutionTime
    public ApiResponseDto<Page<ProductResponseDto>> getProductsByVendorId(@PathVariable("vendorId") Long vendorId, @PageableDefault(sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable){
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Vendor products fetched successfully", productService.getProductsByVendorId(vendorId, pageable));
    }

    @GetMapping("/products")
    @LogExecutionTime
    public ApiResponseDto<Page<ProductResponseDto>> getAllProducts(@PageableDefault(sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "Products fetched successfully", productService.getAllProduct(pageable));
    }

    @GetMapping("/products/recommended")
    @LogExecutionTime
    public ApiResponseDto<Page<ProductResponseDto>> getRecommendedProducts(
            @PageableDefault(sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "Recommended products fetched successfully",
                productService.getRecommendedProducts(pageable)
        );
    }

    @GetMapping("search-products/{keyword}")
    @LogExecutionTime
    public ApiResponseDto<Page<ProductResponseDto>> searchProducts(@PathVariable("keyword") String keyword, Pageable pageable){
        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "Search products fetched successfully",
                productSearchService.search(keyword, pageable)
        );
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
