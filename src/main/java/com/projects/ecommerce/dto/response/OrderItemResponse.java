package com.projects.ecommerce.dto.response;


import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public  class OrderItemResponse {
    private Long productId;
    private String productName;
    private String image;
    private Long variantId;
    private String variantLabel;
    private String sku;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal lineTotal;
}
