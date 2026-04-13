package com.bazaarhub.backend.feature.category.service.impl;

import com.bazaarhub.backend.feature.category.entity.Category;
import com.bazaarhub.backend.feature.category.enums.CategoryStatus;
import com.bazaarhub.backend.feature.category.exception.CategoryAlreadyExistsException;
import com.bazaarhub.backend.feature.category.exception.CategoryNotFoundException;
import com.bazaarhub.backend.feature.category.mapper.CategoryMapper;
import com.bazaarhub.backend.feature.category.repository.CategoryRepository;
import com.bazaarhub.backend.feature.category.resource.request.CategoryRequestDto;
import com.bazaarhub.backend.feature.category.resource.response.CategoryResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryRequestDto categoryRequestDto;
    private CategoryResponseDto categoryResponseDto;

    @BeforeEach
    public void setUp() {
        category = new Category();
        category.setName("Electronics");
        category.setDescription("All electronic items");
        category.setStatus(CategoryStatus.ACTIVE);

        categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Electronics");
        categoryRequestDto.setDescription("All electronic items");

        categoryResponseDto = new CategoryResponseDto(
                1L,
                0L,
                category.getName(),
                category.getDescription(),
                category.getStatus(),
                category.getCreatedAt(),
                category.getModifiedAt()
        );
    }



    @Test
    void createCategory_shouldSaveAndReturnCategory() {
        when(categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(false);
        when(categoryMapper.mapToCategory(categoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.mapToCategoryResponse(category)).thenReturn(categoryResponseDto);

        CategoryResponseDto result = categoryService.createCategory(categoryRequestDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(category.getName(), result.getName());
        Assertions.assertEquals(category.getDescription(), result.getDescription());
        Assertions.assertEquals(CategoryStatus.ACTIVE, result.getStatus());

        verify(categoryRepository, times(1)).existsByNameIgnoreCase("Electronics");
        verify(categoryMapper, times(1)).mapToCategory(categoryRequestDto);
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(categoryMapper, times(1)).mapToCategoryResponse(category);
    }

    @Test
    void createCategory_shouldThrowException_whenNameAlreadyExists() {
        when(categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(true);

        Assertions.assertThrows(CategoryAlreadyExistsException.class,
                () -> categoryService.createCategory(categoryRequestDto));

        verify(categoryRepository, times(1)).existsByNameIgnoreCase("Electronics");
        verify(categoryRepository, never()).save(any(Category.class));
        verify(categoryMapper, never()).mapToCategory(any());
        verify(categoryMapper, never()).mapToCategoryResponse(any());
    }



    @Test
    void deleteCategoryById_shouldSoftDeleteCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategoryById(1L);

        Assertions.assertEquals(CategoryStatus.DELETED, category.getStatus());

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void deleteCategoryById_shouldThrowException_whenCategoryNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(CategoryNotFoundException.class,
                () -> categoryService.deleteCategoryById(99L));

        verify(categoryRepository, times(1)).findById(99L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategoryById_shouldThrowException_whenCategoryAlreadyDeleted() {
        category.setStatus(CategoryStatus.DELETED);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Assertions.assertThrows(CategoryNotFoundException.class,
                () -> categoryService.deleteCategoryById(1L));

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).save(any(Category.class));
    }
}
