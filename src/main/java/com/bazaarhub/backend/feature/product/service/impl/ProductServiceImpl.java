package com.bazaarhub.backend.feature.product.service.impl;

import com.bazaarhub.backend.config.CacheConfig;
import com.bazaarhub.backend.feature.category.entity.Category;
import com.bazaarhub.backend.feature.category.exception.CategoryNotFoundException;
import com.bazaarhub.backend.feature.category.repository.CategoryRepository;
import com.bazaarhub.backend.feature.customerProfile.entity.CustomerProfile;
import com.bazaarhub.backend.feature.customerProfile.exception.CustomerProfileNotFoundException;
import com.bazaarhub.backend.feature.customerProfile.repository.CustomerProfileRepository;
import com.bazaarhub.backend.feature.product.entity.Product;
import com.bazaarhub.backend.feature.product.exception.*;
import com.bazaarhub.backend.feature.product.mapper.ProductMapper;
import com.bazaarhub.backend.feature.product.repository.ProductRepository;
import com.bazaarhub.backend.feature.product.resource.request.ProductRequestDto;
import com.bazaarhub.backend.feature.product.resource.response.ProductResponseDto;
import com.bazaarhub.backend.feature.product.service.ProductSearchService;
import com.bazaarhub.backend.feature.product.service.ProductService;
import com.bazaarhub.backend.feature.vendorProfile.entity.Vendor;
import com.bazaarhub.backend.feature.vendorProfile.exception.VendorNotFoundException;
import com.bazaarhub.backend.feature.vendorProfile.repository.VendorRepository;
import com.bazaarhub.backend.shared.enums.ProductStatus;
import com.bazaarhub.backend.shared.exception.ClientValidationException;
import com.bazaarhub.backend.shared.exception.InsufficientStockException;
import com.bazaarhub.backend.shared.service.MinioService;
import com.bazaarhub.backend.shared.utils.AuthUtil;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final VendorRepository vendorRepository;
    private final CategoryRepository categoryRepository;
    private final ProductSearchService productSearchService;
    private final CustomerProfileRepository customerProfileRepository;
    private final MinioService minioService;

    private static final String INVALID_PRODUCT_INPUT = "One or more inputs are invalid.";

    @Override
    @Transactional
    @CachePut(cacheNames = CacheConfig.PRODUCT_CACHE_NAME, key = "#result.id")
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto, MultipartFile file) {
        log.info("Creating new product.");

        Vendor vendor = vendorRepository.findById(productRequestDto.getVendorId()).orElseThrow(() -> {
            log.error("Vendor not found. vendorId :{}", productRequestDto.getVendorId());
            return new VendorNotFoundException("Vendor not found.");
        });
        if (file == null || file.isEmpty()) {
            log.error("Product image not found.");
            throw new ClientValidationException(INVALID_PRODUCT_INPUT);
        }
        String imageUrl = minioService.uploadFile(file);
        if (productRequestDto.getDiscountPrice() != null && productRequestDto.getDiscountPrice().compareTo(productRequestDto.getPrice()) > 0) {
            log.error(" Discount Price invalid : {}", productRequestDto.getDiscountPrice());
            throw new ClientValidationException(INVALID_PRODUCT_INPUT);
        }
        Category category = categoryRepository.findById(productRequestDto.getCategoryId()).orElseThrow(() -> {
            log.error("Category does not exists. categoryId : {}", productRequestDto.getCategoryId());
            return new CategoryNotFoundException("Category does not exists.");
        });

        Product product = productMapper.mapToProduct(productRequestDto);
        product.setCategory(category);
        product.setVendor(vendor);
        product.setImageUrl(imageUrl);
        product.setStatus(ProductStatus.ACTIVE);
        Product newProduct = productRepository.save(product);

        try {
            productSearchService.indexProduct(newProduct);
        } catch (Exception e) {
            log.error("Elasticsearch indexing failed, product saved to DB only: {}", e.getMessage());
            e.printStackTrace();
        }
        log.info("Product created successfully. productId: {}", newProduct.getId());
        return productMapper.mapToProductResponse(newProduct);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.PRODUCT_CACHE_NAME, key = "#productId")
    public ProductResponseDto getProductById(Long productId) {
        log.info("Fetching product. productId: {}", productId);
        Product product = productRepository.findByIdAndStatusNot(productId, ProductStatus.DELETED).orElseThrow(() -> {
            log.error("Product not found. productId: {}", productId);
            return new ProductNotFoundException("Product not found.");
        });

        ProductResponseDto productResponseDto = productMapper.mapToProductResponse(product);

        productResponseDto.setImageUrl(
                minioService.getImageUrl(product.getImageUrl())
        );

        return productResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductsByVendorId(Long vendorId, Pageable pageable) {
        log.info("Fetching product. vendorId: {}", vendorId);
        return productRepository.findByVendor_IdAndStatusNot(vendorId, ProductStatus.DELETED, pageable).map(product -> {
            ProductResponseDto productResponseDto =
                    productMapper.mapToProductResponse(product);

            productResponseDto.setImageUrl(
                    minioService.getImageUrl(product.getImageUrl())
            );

            return productResponseDto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProduct(Pageable pageable) {
        return productRepository.findByStatusNot(ProductStatus.DELETED, pageable).map(product -> {
            ProductResponseDto productResponseDto =
                    productMapper.mapToProductResponse(product);

            productResponseDto.setImageUrl(
                    minioService.getImageUrl(product.getImageUrl())
            );

            return productResponseDto;
        });
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CacheConfig.PRODUCT_CACHE_NAME, key = "#productId")
    public ProductResponseDto updateProductById(Long productId, ProductRequestDto productRequestDto) {
        log.info("Updating product. productId: {}", productId);
        Product product = productRepository.findByIdAndStatusNot(productId, ProductStatus.DELETED).orElseThrow(() -> {
            log.error("Product not found. productId: {}", productId);
            return new ProductNotFoundException("Product not found.");
        });
        if (productRequestDto.getDiscountPrice() != null && productRequestDto.getDiscountPrice().compareTo(productRequestDto.getPrice()) > 0) {
            log.error(" Discount Price invalid : {}", productRequestDto.getDiscountPrice());
            throw new ClientValidationException(INVALID_PRODUCT_INPUT);
        }

        Vendor vendor = vendorRepository.findById(productRequestDto.getVendorId()).orElseThrow(() -> {
            log.error("Vendor not found. vendorId: {}", productRequestDto.getVendorId());
            return new VendorNotFoundException("Vendor not found");
        });

        Category category = categoryRepository.findById(productRequestDto.getCategoryId()).orElseThrow(() -> {
            log.error("Category not found. categoryId: {}", productRequestDto.getCategoryId());
            return new CategoryNotFoundException("Category not found.");
        });

        product.setName(productRequestDto.getName());
        product.setDescription(productRequestDto.getDescription());
        product.setPrice(productRequestDto.getPrice());
        product.setDiscountPrice(productRequestDto.getDiscountPrice());
        product.setStockQuantity(productRequestDto.getStockQuantity());
        product.setCategory(category);
        product.setVendor(vendor);

        Product updatedProduct = productRepository.save(product);
        // productSearchService.indexProduct(updatedProduct);
        log.info("Product updated successfully. productId: {}", product.getId());
        return productMapper.mapToProductResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void reserveStock(Long productId, Integer quantity) {
        int updatedRows =
                productRepository.reserveStock(productId, quantity);

        if (updatedRows == 0) {
            throw new InsufficientStockException(
                    "Insufficient stock."
            );
        }
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.PRODUCT_CACHE_NAME, key = "#productId")
    public void restoreStock(Long productId, Integer quantity) {
        int updatedRows =
                productRepository.restoreStock(productId, quantity);

        if (updatedRows == 0) {
            throw new ProductNotFoundException("Product not found.");
        }
        log.info("Product quantity updated successfully. productId: {}", productId);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.PRODUCT_CACHE_NAME, key = "#productId")
    public void deleteProductById(Long productId) {
        log.info("Deleting product. productId: {}", productId);
        Product product = productRepository.findByIdAndStatusNot(productId, ProductStatus.DELETED).orElseThrow(() -> {
            log.error("Product not found. productId: {}", productId);
            return new ProductNotFoundException("Product not found.");
        });
        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);
        productSearchService.removeProduct(product.getId());
        log.info("Deleted product of id: {}", productId);

    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getRecommendedProducts(Pageable pageable) {
        Long userId = AuthUtil.getCurrentUserId();
        CustomerProfile customerProfile = customerProfileRepository.findByUser_Id(userId).orElseThrow(() -> {
            log.error("Customer profile not found. userId: {}",userId);
            return new CustomerProfileNotFoundException("Customer profile not found.");
        });

        if (customerProfile.getPreferences() == null || customerProfile.getPreferences().isEmpty()) {
            return productRepository.findByStatusNot(ProductStatus.DELETED, pageable)
                    .map(product -> {
                        ProductResponseDto productResponseDto =
                                productMapper.mapToProductResponse(product);

                        productResponseDto.setImageUrl(
                                minioService.getImageUrl(product.getImageUrl())
                        );

                        return productResponseDto;
                    });
        }

        List<Long> preferenceCategoryIds = customerProfile.getPreferences()
                .stream()
                .map(Long::valueOf)
                .toList();

        return productRepository.findByCategory_IdInAndStatusNot(preferenceCategoryIds, ProductStatus.DELETED, pageable)
                .map(product -> {
                    ProductResponseDto productResponseDto =
                            productMapper.mapToProductResponse(product);

                    productResponseDto.setImageUrl(
                            minioService.getImageUrl(product.getImageUrl())
                    );

                    return productResponseDto;
                });
    }
}
