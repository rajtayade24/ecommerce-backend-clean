package com.projects.ecommerce.service;

import com.projects.ecommerce.dto.CategoryDto;
import com.projects.ecommerce.dto.request.RequestCategoryDto;
import com.projects.ecommerce.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CategoryService {
    CategoryDto create(RequestCategoryDto dto, MultipartFile image);

    CategoryDto update(Long id, RequestCategoryDto dto, MultipartFile image);

    Page<CategoryDto> getCategories(Pageable pageable);

    Page<CategoryDto> getCategories(String search, Pageable pageable);

    List<CategoryDto> getAllCategories();

    Category getCategoryById(Long id);

    Category getCategoryBySlug(String slug);

    void delete(Long id);

    Long countCategories();
}
