package com.projects.ecommerce.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {
    private Long productId;
    private String productName;
    private Long variantId;
    private String variantLabel;
    private String sku;
    private BigDecimal unitPrice;
    private Integer quantity;
}
