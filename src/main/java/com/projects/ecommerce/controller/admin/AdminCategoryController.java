package com.projects.ecommerce.controller.admin;

import com.projects.ecommerce.dto.CategoryDto;
import com.projects.ecommerce.dto.request.RequestCategoryDto;
import com.projects.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://localhost:8080",
        "http://127.0.0.1:5500",
        "http://localhost:5173",
        "http://10.91.2.29:5173",
        "http://10.91.2.29:5173/instagram-clone",
        "https://social-media-frontend-nbdo.vercel.app",
        "*"
})
@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryDto> create(@RequestPart("category") @Valid RequestCategoryDto dto,
                                              @RequestPart(value = "image", required = false) MultipartFile images) {
        CategoryDto categoryDto = categoryService.create(dto, images);
        return ResponseEntity.ok(categoryDto);
    }

    @PutMapping(path = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryDto> update(@PathVariable Long id,
                                              @RequestPart("category") RequestCategoryDto dto,
                                              @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        CategoryDto saved = categoryService.update(id, dto, image);

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countCategories() {
        Long count = categoryService.countCategories();
        return ResponseEntity.ok(count);
    }

}
