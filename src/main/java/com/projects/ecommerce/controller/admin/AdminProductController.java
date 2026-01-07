package com.projects.ecommerce.controller.admin;


import com.projects.ecommerce.dto.ProductDto;
import com.projects.ecommerce.dto.request.CreateProductDto;
import com.projects.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProductDto> create(
            @RequestPart("product") @Valid CreateProductDto dto,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        ProductDto created = productService.create(dto, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProductDto> update(
            @PathVariable(name = "id") Long id,
            @RequestPart("product") @Valid CreateProductDto dto,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        ProductDto updated = productService.update(id, dto, images);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countProducts() {
        Long count = productService.countProducts();
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build(); 
    }

}
