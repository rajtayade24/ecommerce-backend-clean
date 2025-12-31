package com.projects.ecommerce.service;

import com.projects.ecommerce.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface OrderService {
    Page<OrderResponse> getUserOrders(Pageable pageable);

    Page<OrderResponse> getAllOrders(Pageable pageable);

    Page<OrderResponse> getAllOrders(String search, String status, Pageable pageable);

    OrderResponse getOrderById(String id);

    OrderResponse markCompleteByOrderNumber(String orderNumber);

    OrderResponse cancelByOrderNumber(String orderNumber);

    Long countOrders();

    BigDecimal getTotalRevenue();

    OrderResponse cancelUserOrder(Long id);

}
