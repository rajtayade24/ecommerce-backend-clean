package com.projects.ecommerce.controller.publicapi;


import com.projects.ecommerce.dto.ProductDto;
import com.projects.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getProducts(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "category", required = false) String categorySlug,
            @RequestParam(name = "isOrganic", required = false) Boolean isOrganic,
            @RequestParam(name = "isFeatured", required = false) Boolean isFeatured,
            @RequestParam(name = "sort", defaultValue = "new") String sortMode, // "new" or "mostOrdered"
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        // default sort is by createdAt desc (handled in service) unless sortMode == "mostOrdered"
        Pageable pageable = PageRequest.of(page, size); // actual sort applied in service
        Page<ProductDto> productsPage = productService.getProducts(search, categorySlug, isOrganic, isFeatured, sortMode, pageable);

        return ResponseEntity.ok(productsPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto dto = productService.getProductDtoById(id);
        return ResponseEntity.ok(dto);
    }

}