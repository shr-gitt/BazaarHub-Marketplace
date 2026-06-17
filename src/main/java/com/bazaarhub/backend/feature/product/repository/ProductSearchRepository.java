package com.bazaarhub.backend.feature.product.repository;

import com.bazaarhub.backend.feature.product.document.ProductDocument;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
    @Query("""
    {
      "multi_match": {
        "query": "?0",
        "fields": ["name^3", "description", "categoryName^2"],
        "fuzziness": "AUTO"
      }
    }
    """)
    Page<ProductDocument> search(String keyword, Pageable pageable);

    @NonNull Page<ProductDocument> searchSimilar(
            ProductDocument productDocument,
            String @NonNull [] fields,
            @NonNull Pageable pageable
    );
}
