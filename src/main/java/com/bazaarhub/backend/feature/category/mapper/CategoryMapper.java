package com.bazaarhub.backend.feature.category.mapper;

import com.bazaarhub.backend.feature.category.entity.Category;
import com.bazaarhub.backend.feature.category.enums.CategoryStatus;
import com.bazaarhub.backend.feature.category.resource.request.CategoryRequestDto;
import com.bazaarhub.backend.feature.category.resource.response.CategoryResponseDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category mapToCategory(CategoryRequestDto dto) {
        Category category = new Category();
        category.setName(dto.getName().trim());
        category.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        category.setStatus(CategoryStatus.ACTIVE);
        return category;
    }

    public CategoryResponseDto mapToCategoryResponse(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getVersion(),
                category.getName(),
                category.getDescription(),
                category.getStatus(),
                category.getCreatedAt(),
                category.getModifiedAt()
        );
    }
}






