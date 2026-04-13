package com.bazaarhub.backend.feature.category.repository;

import com.bazaarhub.backend.feature.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String name);
}

