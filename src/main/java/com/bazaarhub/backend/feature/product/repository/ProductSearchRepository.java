package com.bazaarhub.backend.feature.product.repository;

import com.bazaarhub.backend.feature.product.document.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
    Page<ProductDocument> findByNameContainingOrDescriptionContaining(
            String name,
            String description,
            Pageable pageable
    );
}
