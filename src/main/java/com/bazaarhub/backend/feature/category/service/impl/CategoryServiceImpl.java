package com.bazaarhub.backend.feature.category.service.impl;

import com.bazaarhub.backend.config.CacheConfig;
import com.bazaarhub.backend.feature.category.entity.Category;
import com.bazaarhub.backend.feature.category.enums.CategoryStatus;
import com.bazaarhub.backend.feature.category.exception.CategoryAlreadyExistsException;
import com.bazaarhub.backend.feature.category.exception.CategoryNotFoundException;
import com.bazaarhub.backend.feature.category.mapper.CategoryMapper;
import com.bazaarhub.backend.feature.category.repository.CategoryRepository;
import com.bazaarhub.backend.feature.category.resource.request.CategoryRequestDto;
import com.bazaarhub.backend.feature.category.resource.response.CategoryResponseDto;
import com.bazaarhub.backend.feature.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @CachePut(cacheNames = CacheConfig.CATEGORY_CACHE_NAME, key = "#result.id")
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        if (categoryRepository.existsByNameIgnoreCase(categoryRequestDto.getName().trim())) {
            log.error("Category already exists [name={}]", categoryRequestDto.getName());
            throw new CategoryAlreadyExistsException("Category already exists");
        }

        Category category = categoryMapper.mapToCategory(categoryRequestDto);
        Category saved = categoryRepository.save(category);
        log.info("Category created .");
        return categoryMapper.mapToCategoryResponse(saved);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.CATEGORY_CACHE_NAME, key = "#categoryId")
    public CategoryResponseDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .filter(c -> c.getStatus() != CategoryStatus.DELETED)
                .orElseThrow(() -> {
                    log.error("Category not found [id={}]", categoryId);
                    return new CategoryNotFoundException("Category not found with id: " + categoryId);
                });
        log.info("Fetched category [id={}]", categoryId);
        return categoryMapper.mapToCategoryResponse(category);
    }

    @Override
    public Page<CategoryResponseDto> getAllCategories(Pageable pageable) {
        log.info("Fetching all categories");
        Pageable pages = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        return categoryRepository.findAll(pages)
                .map(categoryMapper::mapToCategoryResponse);

    }

    @Override
    @CachePut(cacheNames = CacheConfig.CATEGORY_CACHE_NAME, key = "#categoryId")
    public CategoryResponseDto updateCategoryById(Long categoryId, CategoryRequestDto categoryRequestDto) {

        Category category = categoryRepository.findById(categoryId)
                .filter(c -> c.getStatus() != CategoryStatus.DELETED)
                .orElseThrow(() -> {
                    log.error("Category not found for update [id={}]", categoryId);
                    return new CategoryNotFoundException("Category not found");
                });

        if (categoryRequestDto.getName() != null) {
            String newName = categoryRequestDto.getName().trim();
            if (!newName.equalsIgnoreCase(category.getName())
                    && categoryRepository.existsByNameIgnoreCase(newName)) {
                log.error("Name already in use [name={}]", newName);
                throw new CategoryAlreadyExistsException("Category already exists");
            }
            category.setName(newName);
        }
        if (categoryRequestDto.getDescription() != null) {
            category.setDescription(categoryRequestDto.getDescription().trim());
        }
        Category updated = categoryRepository.save(category);
        log.info("Category updated [id={}]", updated.getId());
        return categoryMapper.mapToCategoryResponse(updated);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.CATEGORY_CACHE_NAME, key = "#categoryId")
    public void deleteCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .filter(c -> c.getStatus() != CategoryStatus.DELETED)
                .orElseThrow(() -> {
                    log.error("Category not found for deletion [id={}]", categoryId);
                    return new CategoryNotFoundException("Category not found");
                });
        category.setStatus(CategoryStatus.DELETED);
        categoryRepository.save(category);
        log.info("Category soft-deleted [id={}]", categoryId);
    }
}
