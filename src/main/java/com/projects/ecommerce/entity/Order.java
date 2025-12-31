package com.projects.ecommerce.entity;

import com.projects.ecommerce.enums.OrderStatusType;
import com.projects.ecommerce.enums.PaymentMethodType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_user", columnList = "user_id"),
        @Index(name = "idx_orders_order_number", columnList = "order_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ✔ Used by JPA, ✔ Never exposed to users

    @Column(name = "order_number", nullable = false, unique = true, length = 40)
    private String orderNumber; // e.g., ORD-20251215-0001 ✔ Used in UI, invoices, emails ✔ Safer than exposing
                                // DB id

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user; // Order history, Security checks

    private String customer;

    // totals
    @Column(name = "items_total", precision = 19, scale = 2)
    private BigDecimal itemsTotal; // Sum of all item prices (without tax/shipping): Invoice breakdown, Analytics,
                                   // Refund calculations

    @Column(name = "shipping_total", precision = 19, scale = 2)
    private BigDecimal shippingTotal = BigDecimal.ZERO; // Delivery charge, ✔ Can change independently, ✔ Needed for
                                                        // partial refunds

    @Column(name = "tax_total", precision = 19, scale = 2)
    private BigDecimal taxTotal = BigDecimal.ZERO; // GST / VAT / Sales tax: ✔ Legal requirement, ✔ Used in invoices and
                                                   // compliance

    @Column(name = "discount_total", precision = 19, scale = 2)
    private BigDecimal discountTotal = BigDecimal.ZERO; // Coupons / offers: ✔ Allows promo tracking, ✔ Needed for
                                                        // correct net totals

    @Column(name = "grand_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;
    /// Final amount paid: ✔ Must exactly match Stripe amount, ✔ Used for payment
    /// verification+

    @Column(length = 3, nullable = false)
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    private OrderStatusType status; // PENDING, PAID, PROCESSING, SHIPPED... ; /// ✔ Admin dashboard, ✔ Customer
                                    // tracking

    @Enumerated(EnumType.STRING)
    private PaymentMethodType paymentMethod; // STRIPE, COD, UPI...

    @Column(name = "session_id", length = 100)
    private String stripeSessionId; // Checkout session: ✔ Verifies payment flow: ✔ Debugging Stripe issues, ✔
                                    // Redirect validation

    @Column(name = "payment_intent_id", length = 100)
    private String stripePaymentIntentId; // actual transaction ID: Refunds: Webhooks, Disputes, Idempotency

    // @Embedded
    // @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private Address shippingAddress; // embeddable (queryable): ✔ Must be frozen at purchase time, ✔ Prevents issues
                                     // if user edits address later

    private OffsetDateTime paidAt; // payment was confirmed: ✔ SLA tracking, ✔ Financial reporting, ✔ Fraud
                                   // detection

    @CreationTimestamp
    private OffsetDateTime createdAt; // Order creation time: ✔ Sorting, ✔ Analytics, ✔ Auditing

    @UpdateTimestamp
    private OffsetDateTime updatedAt; // Last order update: ✔ Debugging, ✔ SLA enforcement

    @Version
    private Long version; // Optimistic locking: ✔ Prevents double updates ✔ Critical for concurrency
                          // safety

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>(); /// ✔ One order → many items: ✔ Supports multi-product checkout,
                                                       /// ✔ Required for returns
}
