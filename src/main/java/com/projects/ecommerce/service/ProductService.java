package com.projects.ecommerce.service;

import com.projects.ecommerce.dto.CartItemDto;
import com.projects.ecommerce.dto.ProductDto;
import com.projects.ecommerce.dto.request.CreateProductDto;
import com.projects.ecommerce.entity.Product;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ProductDto create(@Valid CreateProductDto dto, MultipartFile[] images);

    public ProductDto update(Long id, @Valid CreateProductDto dto, MultipartFile[] images);

    CartItemDto addToCart(ProductDto dto);

    Page<ProductDto> getProducts(String search, Pageable pageable);

    Page<ProductDto> getProducts(Pageable pageable);

    Product getProductById(Long id);

    ProductDto getProductDtoById(Long id);

    // new method: supports multiple filters and sort modes ("new" (default) or "mostOrdered")
    Page<ProductDto> getProducts(
            String search,
            String categorySlug,
            Boolean isOrganic,
            Boolean isFeatured,
            String sortMode, // "new" | "mostOrdered"
            Pageable pageable
    );

    Long countProducts();

    void delete(Long id);
}
