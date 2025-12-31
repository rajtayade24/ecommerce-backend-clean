package com.projects.ecommerce.specification;

import com.projects.ecommerce.entity.Order;
import com.projects.ecommerce.enums.OrderStatusType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<Order> hasSearch(String search) {
        return (root, query, cb) -> { /// criefia builder
            if (search == null || search.isBlank())
                return null;

            String like = "%" + search.trim().toLowerCase() + "%";
            Join<Object, Object> addressJoin = root.join("shippingAddress", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("orderNumber")), like), // like pattern
                    cb.like(cb.lower(root.get("customer")), like),
                    cb.like(cb.lower(root.get("stripeSessionId")), like),
                    cb.like(cb.lower(root.get("stripePaymentIntentId")), like),
                    cb.like(cb.lower(addressJoin.get("address")), like),
                    cb.like(cb.lower(addressJoin.get("city")), like));
        };
    }

    public static Specification<Order> hasStatus(OrderStatusType status) {
        return (root, query, cb) -> {
            if (status == null)
                return null;
            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * Combine non-null specifications for search + status.
     */
    public static Specification<Order> combine(String search, OrderStatusType status) {
        Specification<Order> spec = (root, query, cb) -> cb.conjunction(); // always true

        Specification<Order> s;

        s = hasSearch(search);
        if (s != null)
            spec = spec.and(s);

        s = hasStatus(status);
        if (s != null)
            spec = spec.and(s);

        return spec;
    }
}
