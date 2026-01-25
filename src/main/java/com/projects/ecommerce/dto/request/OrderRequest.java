package com.projects.ecommerce.dto.request;

import com.projects.ecommerce.dto.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private String currency = "USD";

    private BigDecimal shippingTotal;
    private BigDecimal taxTotal;
    private BigDecimal discountTotal;
    private BigDecimal totalAmount;

    private String paymentMethod; // e.g., STRIPE, COD, UPI

    private AddressDto shippingAddress;

    private List<OrderItemRequest> items;
}
