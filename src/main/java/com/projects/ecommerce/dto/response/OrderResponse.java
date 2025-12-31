package com.projects.ecommerce.dto.response;

import com.projects.ecommerce.dto.AddressDto;
import com.projects.ecommerce.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String orderNumber;
    private String status; // PENDING, PAID, FAILED, SHIPPED...
    private String customer;
    private UserDto user;

    private BigDecimal itemsTotal;
    private BigDecimal shippingTotal;
    private BigDecimal taxTotal;
    private BigDecimal discountTotal;
    private BigDecimal totalAmount;

    private String currency;
    private String paymentMethod;
    private String stripeSessionId;
    private String stripePaymentIntentId;
    private OffsetDateTime paidAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private AddressDto shippingAddress;

    private List<OrderItemResponse> items;
}
