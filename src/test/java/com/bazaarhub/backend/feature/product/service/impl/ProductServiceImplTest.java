package com.bazaarhub.backend.feature.product.service.impl;

import com.bazaarhub.backend.feature.category.entity.Category;
import com.bazaarhub.backend.feature.category.repository.CategoryRepository;
import com.bazaarhub.backend.feature.product.entity.Product;
import com.bazaarhub.backend.feature.product.exception.InvalidDiscountPriceException;
import com.bazaarhub.backend.feature.product.exception.InvalidPriceException;
import com.bazaarhub.backend.feature.product.exception.ProductNotFoundException;
import com.bazaarhub.backend.feature.product.mapper.ProductMapper;
import com.bazaarhub.backend.feature.product.repository.ProductRepository;
import com.bazaarhub.backend.feature.product.resource.request.ProductRequestDto;
import com.bazaarhub.backend.feature.product.resource.response.ProductResponseDto;
import com.bazaarhub.backend.feature.vendor.entity.Vendor;
import com.bazaarhub.backend.feature.vendor.repository.VendorRepository;
import com.bazaarhub.backend.shared.enums.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequestDto productRequestDto;
    private Product product;
    private ProductResponseDto productResponseDto;
    private Category category;
    private Vendor vendor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        vendor = new Vendor();
        vendor.setId(1L);
        category = new Category();
        category.setId(1L);

        productRequestDto = new ProductRequestDto(
                "Bluetooth Speaker",
                "High-quality Bluetooth speaker",
                new BigDecimal("59.99"),
                new BigDecimal("49.99"),
                120,
                "https://example.com/images/speaker.jpg",
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
                "Test Product",
                "Test Desc",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(90),
                10,
                "http://example.com/image.jpg",
                "Test Category",
                ProductStatus.ACTIVE,
                "Test Vendor",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void testCreateProduct() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productMapper.mapToProduct(productRequestDto)).thenReturn(product);
        when(productMapper.mapToProductResponse(product)).thenReturn(productResponseDto);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDto createdProduct = productService.createProduct(productRequestDto);

        assertNotNull(createdProduct);
        assertEquals("Test Product", createdProduct.getName());
        assertEquals("Test Desc", createdProduct.getDescription());
    }

    @Test
    void testCreateProduct_InvalidPrice_ShouldThrowException() {

        productRequestDto.setPrice(new BigDecimal("-59.99"));
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(InvalidPriceException.class, () -> productService.createProduct(productRequestDto));
    }

    @Test
    void testCreateProduct_InvalidDiscountPrice_ShouldThrowException() {

        productRequestDto.setDiscountPrice(new BigDecimal("11100"));
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(InvalidDiscountPriceException.class, () -> productService.createProduct(productRequestDto));
    }

    @Test
    void testGetProductById() {
        when(productRepository.findByIdAndStatusNot(1L, ProductStatus.DELETED)).thenReturn(Optional.of(product));
        when(productMapper.mapToProductResponse(product)).thenReturn(productResponseDto);

        ProductResponseDto fetchedProduct = productService.getProductById(1L);
        assertNotNull(fetchedProduct);
        assertEquals("Test Product", fetchedProduct.getName());
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
        when(productMapper.mapToProduct(any(ProductRequestDto.class))).thenReturn(product);
        when(productMapper.mapToProductResponse(product)).thenReturn(productResponseDto);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDto updatedProduct = productService.updateProductById(1L, productRequestDto);
        assertNotNull(updatedProduct);
        assertEquals("Test Product", updatedProduct.getName());
    }

    @Test
    void testDeleteProductById() {
        when(productRepository.findByIdAndStatusNot(1L, ProductStatus.DELETED)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.deleteProductById(1L);

        verify(productRepository, times(1)).save(any(Product.class));  // Verify that save was called once
    }

    @Test
    void testDeleteProductById_NotFound() {
        when(productRepository.findByIdAndStatusNot(1L, ProductStatus.DELETED)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProductById(1L));
    }


}