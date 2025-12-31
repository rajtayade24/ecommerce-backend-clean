package com.projects.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "text")
    private String description;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    private boolean isFeatured;
    private boolean isOrganic;

    @Embedded
    private ProductNutrition nutrition;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "url")
    private List<String> images = new ArrayList<>();

    //    Product -> variants -> ProductVariant -> product -> variants -> ProductVariant -> ...
//            …which creates an infinite nesting (or very deep, >500) that Jackson cannot handle.
//    This is a common problem with bidirectional relationships in JPA/Hibernate.
    @JsonManagedReference
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    private List<ProductVariant> variants = new ArrayList<>();
}
