package com.projects.ecommerce.service.impl;

import com.projects.ecommerce.dto.response.OrderResponse;
import com.projects.ecommerce.entity.Order;
import com.projects.ecommerce.entity.User;
import com.projects.ecommerce.enums.OrderStatusType;
import com.projects.ecommerce.repository.OrderRepository;
import com.projects.ecommerce.service.OrderService;
import com.projects.ecommerce.service.UserService;
import com.projects.ecommerce.specification.OrderSpecification;
import com.projects.ecommerce.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserService userService;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final MapperUtil mapper;

    public Page<OrderResponse> getUserOrders(Pageable pageable) {
        User user = userService.getCurrentUser();

        Page<Order> ordersPage = orderRepository.findByUser(user, pageable);
        return ordersPage.map(mapper::mapOrderToResponse);
    }

    @Override
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return page.map(mapper::mapOrderToResponse);
    }

    @Override
    public Page<OrderResponse> getAllOrders(String search, String status, Pageable pageable) {
        OrderStatusType statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = OrderStatusType.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new RuntimeException("Invalid order status: " + status);
            }
        }
        Specification<Order> spec = OrderSpecification.combine(search, statusEnum);

        Page<Order> page = orderRepository.findAll(spec, pageable);
        return page.map(mapper::mapOrderToResponse);
    }

    @Override
    public OrderResponse getOrderById(String id) {
        Order order = orderRepository.findByOrderNumber(String.valueOf(id)).orElseThrow();

        return mapper.mapOrderToResponse(order);
    }

    @Override
    public OrderResponse cancelUserOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Order not found with id: " + id));

        if (order.getStatus() == OrderStatusType.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }

        if (order.getStatus() == OrderStatusType.COMPLETED) {
            throw new IllegalStateException("Delivered orders cannot be cancelled");
        }

        order.setStatus(OrderStatusType.CANCELLED);
        order.setUpdatedAt(OffsetDateTime.now());

        Order savedOrder = orderRepository.save(order);

        return mapper.mapOrderToResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse markCompleteByOrderNumber(String orderNumber) {
        Order o = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("orderNumber" + orderNumber));

        if (o.getStatus() == OrderStatusType.COMPLETED) {
            throw new RuntimeException("Order already completed");
        }
        if (o.getStatus() == OrderStatusType.CANCELLED) {
            throw new RuntimeException("Cancelled order cannot be completed");
        }
        o.setStatus(OrderStatusType.COMPLETED);

        // if you want to set paidAt when completing:
        if (o.getPaidAt() == null) {
            // keep as-is or set to now if semantics require
            // o.setPaidAt(OffsetDateTime.now());
        }

        Order saved = orderRepository.save(o);
        log.info("Order {} marked as COMPLETED", orderNumber);
        return mapper.mapOrderToResponse(saved);
    }

    @Override
    @Transactional
    public OrderResponse cancelByOrderNumber(String orderNumber) {
        Order o = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException(orderNumber));

        if (o.getStatus() == OrderStatusType.CANCELLED) {
            throw new RuntimeException("Order already cancelled");
        }
        if (o.getStatus() == OrderStatusType.COMPLETED) {
            throw new RuntimeException("Completed order cannot be cancelled");
        }

        o.setStatus(OrderStatusType.CANCELLED);
        // Optionally: create refund, restock items, notify user, append audit log etc.

        Order saved = orderRepository.save(o);
        log.info("Order {} cancelled", orderNumber);
        return mapper.mapOrderToResponse(saved);
    }

    public Long countOrders() {
        return orderRepository.count();
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return orderRepository.calculateRevenueByStatuses(
                List.of(
                        OrderStatusType.PAID,
                        OrderStatusType.COMPLETED));
    }

}
