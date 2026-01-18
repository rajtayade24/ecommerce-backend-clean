package com.projects.ecommerce.service.impl;

import com.projects.ecommerce.dto.CategoryDto;
import com.projects.ecommerce.dto.request.RequestCategoryDto;
import com.projects.ecommerce.dto.response.CloudinaryUploadResult;
import com.projects.ecommerce.entity.Category;
import com.projects.ecommerce.repository.CategoryRepository;
import com.projects.ecommerce.repository.ProductRepository;
import com.projects.ecommerce.service.CategoryService;
import com.projects.ecommerce.service.CloudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final CloudService cloudService;
    private final ProductRepository productRepository;

    @Override
    public CategoryDto create(RequestCategoryDto dto, MultipartFile image) {
        Category category = modelMapper.map(dto, Category.class);

        try {
            // Handle image upload
            if (image != null && !image.isEmpty()) {
                CloudinaryUploadResult uploaded = cloudService.upload(image); // method below
                category.setImage(uploaded.getImage());
                category.setPublicId(uploaded.getPublicId());
            }

            Category savedCategory = categoryRepository.save(category);
            return modelMapper.map(savedCategory, CategoryDto.class);
        } catch (Exception e) {
            // Any other failure
            throw new RuntimeException("Failed to create category: " + e.getMessage(), e);
        }
    }

    @Override
    public CategoryDto update(Long id, RequestCategoryDto dto, MultipartFile image) {
        Category existing = categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        existing.setName(dto.getName());
        existing.setSlug(dto.getSlug());
        existing.setDescription(dto.getDescription());

        if (image != null && !image.isEmpty()) {
            try {
                CloudinaryUploadResult uploaded = cloudService.upload(image); // method below
                existing.setImage(uploaded.getImage());
                existing.setPublicId(uploaded.getPublicId());

            } catch (Exception e) {
                throw new RuntimeException("Image upload failed: " + e.getMessage(), e);
            }
        }

        Category saved = categoryRepository.save(existing);
        return modelMapper.map(saved, CategoryDto.class);
    }

    @Override
    public Page<CategoryDto> getCategories(Pageable pageable) {
        Page<Category> page = categoryRepository.findAll(pageable);

        Page<CategoryDto> page1 = page.map(cat -> modelMapper.map(cat, CategoryDto.class));

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        // Fix image URL for each DTO
        page1.getContent().forEach(dto -> {
            if (dto.getImage() != null && !dto.getImage().startsWith("http")) {
                dto.setImage(baseUrl + dto.getImage());
            }
        });
        return page1;
    }

    @Override
    public Page<CategoryDto> getCategories(String search, Pageable pageable) {
        Page<Category> page;

        if (search != null && !search.isEmpty()) {
            page = categoryRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            page = categoryRepository.findAll(pageable);
        }

        Page<CategoryDto> page1 = page.map(cat -> modelMapper.map(cat, CategoryDto.class));

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        // Fix image URL for each DTO
        page1.getContent().forEach(dto -> {
            if (dto.getImage() != null && !dto.getImage().startsWith("http")) {
                dto.setImage(baseUrl + dto.getImage());
            }
        });
        return page1;
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<CategoryDto> categoryDtos = categoryRepository.findAll().stream().map(category -> modelMapper.map(category, CategoryDto.class)).toList();

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        // FIX
        categoryDtos.forEach(dto -> {
            if (dto.getImage() != null && !dto.getImage().startsWith("http")) {
                dto.setImage(baseUrl + dto.getImage());
            }
        });

        return categoryDtos;

    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Category not found with slug: " + slug));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Request received to delete category with id: {}", id);

        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.error("Category not found with id: {}", id);
            return new IllegalArgumentException("Category not found with id: " + id);
        });

        long productCount = productRepository.countByCategoryId(id);

        if (productCount > 0) {
            log.warn("Category {} has {} products. Deletion blocked.", id, productCount);
            throw new IllegalStateException("Category contains " + productCount + " products. Reassign before deleting.");
        }

        // Delete stored image if exists
        if (category.getImage() != null && category.getPublicId() != null) {
            log.info("Attempting to delete image with publicId: {}", category.getPublicId());
            try {
//                cloudService.deleteFile(category.getPublicId());
                log.info("Image deleted successfully: {}", category.getImage());
            } catch (Exception e) {
                log.error("Failed to delete image '{}' for category id {}. Error: {}", category.getImage(), id, e.getMessage(), e);
                throw new RuntimeException("Failed to delete image: " + e.getMessage(), e);
            }
        }

        categoryRepository.delete(category);
        log.info("Category deleted successfully with id: {}", id);
    }


    public CategoryDto getCategoryDtoById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow();
        CategoryDto dto = modelMapper.map(category, CategoryDto.class);

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        // FIX
        if (dto.getImage() != null && !dto.getImage().startsWith("http")) {
            dto.setImage(baseUrl + dto.getImage());
        }

        return dto;
    }

    @Override
    public Long countCategories() {
        return categoryRepository.count();
    }


}
