package com.bazaarhub.backend.feature.category.service;

import com.bazaarhub.backend.feature.category.resource.request.CategoryRequestDto;
import com.bazaarhub.backend.feature.category.resource.response.CategoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);

    CategoryResponseDto getCategoryById(Long categoryId);

    Page<CategoryResponseDto> getAllCategories(Pageable pageable);

    CategoryResponseDto updateCategoryById(Long categoryId, CategoryRequestDto categoryRequestDto);

    void deleteCategoryById(Long categoryId);
}
