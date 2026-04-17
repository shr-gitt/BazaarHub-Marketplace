package com.bazaarhub.backend.feature.product.service.impl;

import com.bazaarhub.backend.config.CacheConfig;
import com.bazaarhub.backend.feature.category.entity.Category;
import com.bazaarhub.backend.feature.category.exception.CategoryAlreadyExistsException;
import com.bazaarhub.backend.feature.category.exception.CategoryNotFoundException;
import com.bazaarhub.backend.feature.category.repository.CategoryRepository;
import com.bazaarhub.backend.feature.product.entity.Product;
import com.bazaarhub.backend.feature.product.exception.InvalidDiscountPriceException;
import com.bazaarhub.backend.feature.product.exception.InvalidPriceException;
import com.bazaarhub.backend.feature.product.exception.ProductNotFoundException;
import com.bazaarhub.backend.feature.product.mapper.ProductMapper;
import com.bazaarhub.backend.feature.product.repository.ProductRepository;
import com.bazaarhub.backend.feature.product.resource.request.ProductRequestDto;
import com.bazaarhub.backend.feature.product.resource.response.ProductResponseDto;
import com.bazaarhub.backend.feature.product.service.ProductService;
import com.bazaarhub.backend.feature.vendor.entity.Vendor;
import com.bazaarhub.backend.feature.vendor.exception.VendorNotFoundException;
import com.bazaarhub.backend.feature.vendor.repository.VendorRepository;
import com.bazaarhub.backend.shared.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final VendorRepository vendorRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @CachePut(cacheNames = CacheConfig.PRODUCT_CACHE_NAME, key = "#result.id")
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {

        Vendor vendor = vendorRepository.findById(productRequestDto.getVendorId()).orElseThrow(() -> {
            log.error("Vendor not found of id :{}", productRequestDto.getVendorId());
            return new VendorNotFoundException("Vendor not found.");
        });

        if (productRequestDto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Price invalid : {}", productRequestDto.getPrice());
            throw new InvalidPriceException("Price must be greater than 0.");
        }
        if (productRequestDto.getDiscountPrice() != null && productRequestDto.getDiscountPrice().compareTo(productRequestDto.getPrice()) > 0) {
            log.error(" Discount Price invalid : {}", productRequestDto.getDiscountPrice());
            throw new InvalidDiscountPriceException("Discount price cannot be greater than price.");
        }
        Category category = categoryRepository.findById(productRequestDto.getCategoryId()).orElseThrow(() -> {
            log.error("Category already exists of id : {}", productRequestDto.getCategoryId());
            return new CategoryAlreadyExistsException("Category already exists.");
        });

        Product product = productMapper.mapToProduct(productRequestDto);
        product.setCategory(category);
        product.setVendor(vendor);
        product.setStatus(ProductStatus.ACTIVE);
        Product newProduct = productRepository.save(product);
        return productMapper.mapToProductResponse(newProduct);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.PRODUCT_CACHE_NAME, key = "#productId")
    public ProductResponseDto getProductById(Long productId) {
        Product product = productRepository.findByIdAndStatusNot(productId, ProductStatus.DELETED).orElseThrow(() -> {
            log.error("Product not found of id: {}", productId);
            return new ProductNotFoundException("Product not found.");
        });
        return productMapper.mapToProductResponse(product);
    }

    @Override
    public Page<ProductResponseDto> getAllProduct(Pageable pageable) {
        return productRepository.findByStatusNot(ProductStatus.DELETED, pageable).map(productMapper::mapToProductResponse);
    }

    @Override
    @CachePut(cacheNames = CacheConfig.PRODUCT_CACHE_NAME, key = "#productId")
    public ProductResponseDto updateProductById(Long productId, ProductRequestDto productRequestDto) {
        Product product = productRepository.findByIdAndStatusNot(productId, ProductStatus.DELETED).orElseThrow(() -> {
            log.error("Product not found of id: {}", productId);
            return new ProductNotFoundException("Product not found.");
        });
        if (productRequestDto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }
        if (productRequestDto.getDiscountPrice() != null && productRequestDto.getDiscountPrice().compareTo(productRequestDto.getPrice()) > 0) {
            throw new IllegalArgumentException("Discount price cannot be greater than price.");
        }

        Vendor vendor = vendorRepository.findById(productRequestDto.getVendorId()).orElseThrow(() -> {
            log.error("Vendor not found of id: {}", productRequestDto.getVendorId());
            return new VendorNotFoundException("Vendor not found");
        });

        Category category = categoryRepository.findById(productRequestDto.getCategoryId()).orElseThrow(() -> {
            log.error("Category not found of id: {}", productRequestDto.getCategoryId());
            return new CategoryNotFoundException("Category not found.");
        });

        Product updateProduct = productMapper.mapToProduct(productRequestDto);

        updateProduct.setName(productRequestDto.getName());
        updateProduct.setDescription(productRequestDto.getDescription());
        updateProduct.setPrice(productRequestDto.getPrice());
        updateProduct.setDiscountPrice(productRequestDto.getDiscountPrice());
        updateProduct.setStockQuantity(productRequestDto.getStockQuantity());
        updateProduct.setImageUrl(productRequestDto.getImageUrl());
        updateProduct.setCategory(category);
        updateProduct.setVendor(vendor);

        Product updatedProduct = productRepository.save(product);
        return productMapper.mapToProductResponse(updatedProduct);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.PRODUCT_CACHE_NAME, key = "#productId")
    public void deleteProductById(Long productId) {
        Product product = productRepository.findByIdAndStatusNot(productId, ProductStatus.DELETED).orElseThrow(() -> {
            log.error("Product not found of id: {}", productId);
            return new ProductNotFoundException("Product not found.");
        });
        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);

    }
}
