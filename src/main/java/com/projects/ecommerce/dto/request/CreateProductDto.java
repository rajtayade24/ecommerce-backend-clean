package com.projects.ecommerce.dto.request;

import com.projects.ecommerce.entity.ProductNutrition;
import com.projects.ecommerce.entity.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDto {
    private Long category;
    private String name;
    private String slug;

    private String description;

    private java.math.BigDecimal price;
    private String unit;

    private boolean isFeatured;
    private boolean isOrganic;
    private boolean inStock;

    private ProductNutrition nutrition;

    private List<ProductVariant> variants = new ArrayList<>();
}

