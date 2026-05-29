package com.bazaarhub.backend.feature.product.service.impl;

import com.bazaarhub.backend.feature.category.entity.Category;
import com.bazaarhub.backend.feature.category.repository.CategoryRepository;
import com.bazaarhub.backend.feature.customerProfile.repository.CustomerProfileRepository;
import com.bazaarhub.backend.feature.product.entity.Product;
import com.bazaarhub.backend.feature.product.exception.ProductNotFoundException;
import com.bazaarhub.backend.feature.product.mapper.ProductMapper;
import com.bazaarhub.backend.feature.product.repository.ProductRepository;
import com.bazaarhub.backend.feature.product.resource.request.ProductRequestDto;
import com.bazaarhub.backend.feature.product.resource.response.ProductResponseDto;
import com.bazaarhub.backend.feature.vendorProfile.entity.Vendor;
import com.bazaarhub.backend.feature.vendorProfile.repository.VendorRepository;
import com.bazaarhub.backend.shared.enums.ProductStatus;
import com.bazaarhub.backend.shared.service.MinioService;
import com.bazaarhub.backend.shared.utils.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private CustomerProfileRepository customerProfileRepository;

    @Mock
    private MinioService minioService;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequestDto productRequestDto;
    private Product product;
    private ProductResponseDto productResponseDto;
    private Category category;
    private Vendor vendor;

    @BeforeEach
    void setUp() {
        vendor = new Vendor();
        vendor.setId(1L);
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        productRequestDto = new ProductRequestDto(
                "Bluetooth Speaker",
                "High-quality Bluetooth speaker",
                new BigDecimal("59.99"),
                new BigDecimal("49.99"),
                120,
                1L,
                1L
        );

        product = new Product();
        product.setId(1L);
        product.setName("Bluetooth Speaker");
        product.setDescription("This is speaker");
        product.setPrice(new BigDecimal("59.99"));
        product.setDiscountPrice(new BigDecimal("49.99"));
        product.setStockQuantity(120);
        product.setImageUrl("https://example.com/images/speaker.jpg");
        product.setVendor(vendor);
        product.setCategory(category);
        product.setStatus(ProductStatus.ACTIVE);

        productResponseDto = new ProductResponseDto(
                1L,
                "Bluetooth Speaker",
                "High-quality Bluetooth speaker",
                new BigDecimal("59.99"),
                new BigDecimal("49.99"),
                120,
                "https://example.com/images/speaker.jpg",
                "Electronics",
                ProductStatus.ACTIVE,
                "Test Vendor",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void testCreateProduct() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(minioService.uploadFile(file))
                .thenReturn("https://example.com/images/speaker.jpg");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productMapper.mapToProduct(productRequestDto)).thenReturn(product);
        when(productMapper.mapToProductResponse(product)).thenReturn(productResponseDto);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDto createdProduct = productService.createProduct(productRequestDto, file);

        assertNotNull(createdProduct);
        assertEquals("Bluetooth Speaker", createdProduct.getName());
        assertEquals("High-quality Bluetooth speaker", createdProduct.getDescription());

        assertEquals("https://example.com/images/speaker.jpg", product.getImageUrl());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());

        verify(vendorRepository, times(1)).findById(1L);
        verify(minioService, times(1)).uploadFile(file);
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).mapToProductResponse(product);
    }

    @Test
    void testCreateProduct_InvalidPrice_ShouldThrowException() {

        productRequestDto.setPrice(new BigDecimal("-59.99"));

        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(minioService.uploadFile(file))
                .thenReturn("https://example.com/images/speaker.jpg");

        assertThrows(InvalidPriceException.class, () -> productService.createProduct(productRequestDto, file));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testCreateProduct_InvalidDiscountPrice_ShouldThrowException() {

        productRequestDto.setDiscountPrice(new BigDecimal("11100"));
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(minioService.uploadFile(file))
                .thenReturn("https://example.com/images/speaker.jpg");

        assertThrows(
                InvalidDiscountPriceException.class,
                () -> productService.createProduct(productRequestDto, file)
        );
        verify(productRepository, never()).save(any(Product.class));

    }

    @Test
    void testGetProductById() {
        when(productRepository.findByIdAndStatusNot(1L, ProductStatus.DELETED)).thenReturn(Optional.of(product));
        when(productMapper.mapToProductResponse(product)).thenReturn(productResponseDto);

        ProductResponseDto fetchedProduct = productService.getProductById(1L);
        assertNotNull(fetchedProduct);
        assertEquals("Bluetooth Speaker", fetchedProduct.getName());
        verify(productRepository, times(1))
                .findByIdAndStatusNot(1L, ProductStatus.DELETED);

        verify(productMapper, times(1))
                .mapToProductResponse(product);
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findByIdAndStatusNot(1L, ProductStatus.DELETED)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void testUpdateProductById() {
        when(productRepository.findByIdAndStatusNot(1L, ProductStatus.DELETED)).thenReturn(Optional.of(product));
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.mapToProductResponse(product))
                .thenReturn(productResponseDto);

        ProductResponseDto updatedProduct = productService.updateProductById(1L, productRequestDto);
        assertNotNull(updatedProduct);
        assertEquals("Bluetooth Speaker", updatedProduct.getName());
        assertEquals("High-quality Bluetooth speaker", product.getDescription());
        assertEquals(new BigDecimal("59.99"), product.getPrice());
        assertEquals(new BigDecimal("49.99"), product.getDiscountPrice());
        assertEquals(120, product.getStockQuantity());

        verify(productRepository, times(1))
                .findByIdAndStatusNot(1L, ProductStatus.DELETED);

        verify(vendorRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);
        verify(productMapper, times(1)).mapToProductResponse(product);
    }

    @Test
    void testDeleteProductById() {
        when(productRepository.findByIdAndStatusNot(1L, ProductStatus.DELETED)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.deleteProductById(1L);
        assertEquals(ProductStatus.DELETED, product.getStatus());

        verify(productRepository, times(1))
                .findByIdAndStatusNot(1L, ProductStatus.DELETED);

        verify(productRepository, times(1)).save(any(Product.class));  // Verify that save was called once
    }

    @Test
    void testDeleteProductById_NotFound() {
        when(productRepository.findByIdAndStatusNot(1L, ProductStatus.DELETED)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProductById(1L));
    }


}