package com.bazaarhub.backend.feature.product.service.impl;

import com.bazaarhub.backend.config.CacheConfig;
import com.bazaarhub.backend.feature.category.entity.Category;
import com.bazaarhub.backend.feature.category.exception.CategoryAlreadyExistsException;
import com.bazaarhub.backend.feature.category.exception.CategoryNotFoundException;
import com.bazaarhub.backend.feature.category.repository.CategoryRepository;
import com.bazaarhub.backend.feature.customerProfile.entity.CustomerProfile;
import com.bazaarhub.backend.feature.customerProfile.repository.CustomerProfileRepository;
import com.bazaarhub.backend.feature.product.entity.Product;
import com.bazaarhub.backend.feature.product.exception.InvalidDiscountPriceException;
import com.bazaarhub.backend.feature.product.exception.InvalidPriceException;
import com.bazaarhub.backend.feature.product.exception.ProductNotFoundException;
import com.bazaarhub.backend.feature.product.mapper.ProductMapper;
import com.bazaarhub.backend.feature.product.repository.ProductRepository;
import com.bazaarhub.backend.feature.product.resource.request.ProductRequestDto;
import com.bazaarhub.backend.feature.product.resource.response.ProductResponseDto;
import com.bazaarhub.backend.feature.product.service.ProductService;
import com.bazaarhub.backend.feature.vendorProfile.entity.Vendor;
import com.bazaarhub.backend.feature.vendorProfile.exception.VendorNotFoundException;
import com.bazaarhub.backend.feature.vendorProfile.repository.VendorRepository;
import com.bazaarhub.backend.shared.enums.ProductStatus;
import com.bazaarhub.backend.shared.service.MinioService;
import com.bazaarhub.backend.shared.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final VendorRepository vendorRepository;
    private final CategoryRepository categoryRepository;
    private final AuthUtil authUtil;
    private final CustomerProfileRepository customerProfileRepository;
    private final MinioService minioService;

    @Override
    @CachePut(cacheNames = CacheConfig.PRODUCT_CACHE_NAME, key = "#result.id")
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto, MultipartFile file) {

        log.info("Creating new product.");
        Vendor vendor = vendorRepository.findById(productRequestDto.getVendorId()).orElseThrow(() -> {
            log.error("Vendor not found of id :{}", productRequestDto.getVendorId());
            return new VendorNotFoundException("Vendor not found.");
        });
        String imageUrl = minioService.uploadFile(file);
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
        product.setImageUrl(imageUrl);
        product.setStatus(ProductStatus.ACTIVE);
        Product newProduct = productRepository.save(product);
        log.info("Product created successfully of id: {}", newProduct.getId());
        return productMapper.mapToProductResponse(newProduct);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.PRODUCT_CACHE_NAME, key = "#productId")
    public ProductResponseDto getProductById(Long productId) {
        log.info("Fetching product of id: {}", productId);
        Product product = productRepository.findByIdAndStatusNot(productId, ProductStatus.DELETED).orElseThrow(() -> {
            log.error("Product not found of id: {}", productId);
            return new ProductNotFoundException("Product not found.");
        });
        return productMapper.mapToProductResponse(product);
    }

    @Override
    public Page<ProductResponseDto> getAllProduct(Pageable pageable) {
        log.info("Fetching all products.");
        return productRepository.findByStatusNot(ProductStatus.DELETED, pageable).map(productMapper::mapToProductResponse);
    }

    @Override
    @CachePut(cacheNames = CacheConfig.PRODUCT_CACHE_NAME, key = "#productId")
    public ProductResponseDto updateProductById(Long productId, ProductRequestDto productRequestDto) {
        log.info("Fetching product of id: {}", productId);
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

        product.setName(productRequestDto.getName());
        product.setDescription(productRequestDto.getDescription());
        product.setPrice(productRequestDto.getPrice());
        product.setDiscountPrice(productRequestDto.getDiscountPrice());
        product.setStockQuantity(productRequestDto.getStockQuantity());
        product.setImageUrl(product.getImageUrl());
        product.setCategory(category);
        product.setVendor(vendor);

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully of id: {}", product.getId());
        return productMapper.mapToProductResponse(updatedProduct);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.PRODUCT_CACHE_NAME, key = "#productId")
    public void deleteProductById(Long productId) {
        log.info("Fetching product of id: {}", productId);
        Product product = productRepository.findByIdAndStatusNot(productId, ProductStatus.DELETED).orElseThrow(() -> {
            log.error("Product not found of id: {}", productId);
            return new ProductNotFoundException("Product not found.");
        });
        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);
        log.info("Deleted product of id: {}", productId);

    }

    @Override
    public Page<ProductResponseDto> getRecommendedProducts(Pageable pageable) {

        log.info("Fetching recommended products.");
        Long userId = AuthUtil.getCurrentUserId();
        CustomerProfile customerProfile = customerProfileRepository.findByUserId(userId);

        if (customerProfile == null ||
                customerProfile.getPreferences() == null ||
                customerProfile.getPreferences().isEmpty()) {

            return productRepository.findByStatusNot(ProductStatus.DELETED, pageable)
                    .map(productMapper::mapToProductResponse);
        }

        List<Long> preferenceCategoryIds = customerProfile.getPreferences()
                .stream()
                .map(Long::valueOf)
                .toList();

        return productRepository.findByCategory_IdInAndStatusNot(preferenceCategoryIds, ProductStatus.DELETED, pageable)
                .map(productMapper::mapToProductResponse);

    }
}
