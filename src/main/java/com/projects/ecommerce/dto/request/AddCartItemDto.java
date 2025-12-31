package com.projects.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCartItemDto {
    @NotNull
    private Long productId;

    // optional
    private Long variantId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
