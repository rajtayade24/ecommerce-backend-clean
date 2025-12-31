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

    private BigDecimal value = BigDecimal.valueOf(0);

    private String unit = "unit";

    private double price;           

    private Integer stock = 0;

    private String label;
    private String  sku;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "product_id")
    private Product product;
}