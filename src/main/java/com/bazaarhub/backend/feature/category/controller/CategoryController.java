package com.bazaarhub.backend.feature.category.controller;

import com.bazaarhub.backend.feature.category.resource.request.CategoryRequestDto;
import com.bazaarhub.backend.feature.category.resource.response.CategoryResponseDto;
import com.bazaarhub.backend.feature.category.service.CategoryService;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/category")
    public ApiResponseDto<CategoryResponseDto> createCategory(
            @Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value, "Category created successfully", categoryService.createCategory(categoryRequestDto));
    }


    @GetMapping("/category/{id}")
    public ApiResponseDto<CategoryResponseDto> getCategoryById(
            @PathVariable("id") Long categoryId) {
        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value, "Category fetched successfully", categoryService.getCategoryById(categoryId));
    }


    @GetMapping("/categories")
    public ApiResponseDto<Page<CategoryResponseDto>> getAllCategories(@PageableDefault(sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponseDto<>(ResponseStatus.SUCCESS.value, "All categories fetched successfully", categoryService.getAllCategories(pageable));
    }

    @PostMapping("/category/{id}")
    public ApiResponseDto<CategoryResponseDto> updateCategoryById(
            @PathVariable("id") Long categoryId,
            @Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value, "Category updated successfully", categoryService.updateCategoryById(categoryId, categoryRequestDto));
    }

    @DeleteMapping("/category/{id}")
    public ApiResponseDto<String> deleteCategoryById(
            @PathVariable("id") Long categoryId) {

        categoryService.deleteCategoryById(categoryId);
        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value, "Category deleted successfully");
    }

}
