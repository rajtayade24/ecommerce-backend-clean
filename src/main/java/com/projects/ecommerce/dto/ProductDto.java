package com.projects.ecommerce.dto;

import com.projects.ecommerce.entity.Category;
import com.projects.ecommerce.entity.ProductNutrition;
import com.projects.ecommerce.entity.ProductVariant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductDto {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private Long category;


    private java.math.BigDecimal price;
    private String unit;

    private long inStock;
    private boolean isFeatured;
    private boolean isOrganic;

    private ProductNutrition nutrition;

    private List<String> images = new ArrayList<>();

    private List<ProductVariant> variants = new ArrayList<>();
    private OffsetDateTime createdAt;
}

