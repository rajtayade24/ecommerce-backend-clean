package com.projects.ecommerce.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CheckoutPreviewRequest {
    private List<ItemRequest> items;

    @Data
    public static class ItemRequest {
        private Long productId;
        private Long variantId;
        private int quantity;
    }
}
