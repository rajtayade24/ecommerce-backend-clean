package com.projects.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // owning side
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // snapshot fields (do not rely on live product tables)
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "variant_id")
    private Long variantId;

    @Column(name = "variant_label")
    private String variantLabel;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 19, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "line_total", precision = 19, scale = 2)
    private BigDecimal lineTotal; // unitPrice * quantity

    @Column(name = "sku")
    private String sku; /// Stock Keeping Unit:  A unique identifier for a product or a specific variant of a product, used internally to track inventory.
}
