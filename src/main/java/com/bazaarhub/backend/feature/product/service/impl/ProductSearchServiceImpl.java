package com.bazaarhub.backend.feature.product.service.impl;

import com.bazaarhub.backend.feature.product.document.ProductDocument;
import com.bazaarhub.backend.feature.product.entity.Product;
import com.bazaarhub.backend.feature.product.mapper.ProductMapper;
import com.bazaarhub.backend.feature.product.repository.ProductRepository;
import com.bazaarhub.backend.feature.product.repository.ProductSearchRepository;
import com.bazaarhub.backend.feature.product.resource.response.ProductResponseDto;
import com.bazaarhub.backend.feature.product.service.ProductSearchService;
import com.bazaarhub.backend.shared.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {

    private final ProductSearchRepository productSearchRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final MinioService minioService;

    @Override
    public void indexProduct(Product product) {
        ProductDocument doc = ProductDocument.builder()
                .id(product.getId().toString())
                .name(product.getName())
                .description(product.getDescription())
                .categoryName("product.getCategory().getName()")
                .build();
        productSearchRepository.save(doc);
    }

    @Override
    public void removeProduct(Long productId) {
        productSearchRepository.deleteById(productId.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> search(String keyword, Pageable pageable) {
        ProductDocument productDocument = new ProductDocument();
        Page<ProductDocument> documentPage = productSearchRepository
                .search(keyword, pageable);

        List<Long> ids = documentPage.getContent()
                .stream()
                .map(doc -> Long.parseLong(doc.getId()))
                .toList();

        List<ProductResponseDto> products = productRepository.findAllById(ids)
                .stream()
                .map(product -> {
                    ProductResponseDto dto = productMapper.mapToProductResponse(product);
                    dto.setImageUrl(minioService.getImageUrl(product.getImageUrl()));
                    return dto;
                })
                .toList();

        return new PageImpl<>(products, pageable, documentPage.getTotalElements());
    }
}