package com.projects.ecommerce.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Setter
@Getter
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal value = BigDecimal.valueOf(0);

    @Column(nullable = false)
    private String unit = "unit";

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private Integer stock = 0;

    private String label;

    private String  sku;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}