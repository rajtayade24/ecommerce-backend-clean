package com.projects.ecommerce.repository;

import com.projects.ecommerce.entity.Order;
import com.projects.ecommerce.entity.Product;
import com.projects.ecommerce.entity.User;
import com.projects.ecommerce.enums.OrderStatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByStripeSessionId(String stripeSessionId);

    Optional<Order> findByStripePaymentIntentId(String paymentIntentId);

    boolean existsByStripePaymentIntentId(String paymentIntentId);

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("""
                SELECT COALESCE(SUM(o.totalAmount), 0)
                FROM Order o
                WHERE o.status IN :statuses
            """)
    BigDecimal calculateRevenueByStatuses(
            @Param("statuses") List<OrderStatusType> statuses);

    Page<Order> findByUser(User user, Pageable pageable);
}

// @Query("""
// SELECT o FROM Order o
// WHERE ( LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%'))
// OR LOWER(o.customer) LIKE LOWER(CONCAT('%', :search, '%'))
// OR LOWER(o.stripeSessionId) LIKE LOWER(CONCAT('%', :search, '%'))
// OR LOWER(o.stripePaymentIntentId) LIKE LOWER(CONCAT('%', :search, '%'))
// OR LOWER(o.shippingAddress.address) LIKE LOWER(CONCAT('%', :search, '%'))
// OR LOWER(o.shippingAddress.city) LIKE LOWER(CONCAT('%', :search, '%'))
// )
// AND (:status IS NULL OR o.status = :status)
// """)
// Page<Order> searchOrders(
// @Param("search") String search,
// @Param("status") OrderStatusType status,
// Pageable pageable
// );
