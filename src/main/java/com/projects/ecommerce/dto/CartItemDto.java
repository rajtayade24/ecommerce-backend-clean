package com.projects.ecommerce.dto;

import com.projects.ecommerce.entity.Product;
import com.projects.ecommerce.entity.ProductVariant;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class CartItemDto {
    private Long id;

    private ProductDto product;
    private ProductVariant variant;
    private Integer quantity;
    private double unitPrice; // price for 1 unit (captured at add time)
    private double totalPrice; // unitPrice * quantity
    private OffsetDateTime createdAt = OffsetDateTime.now();

}
