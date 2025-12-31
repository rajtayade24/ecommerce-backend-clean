package com.projects.ecommerce.repository;


import com.projects.ecommerce.entity.Product;
import com.projects.ecommerce.repository.custom.ProductRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

//production-ready, paginated + filterable implementation using Spring Data JPA Specification
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>, ProductRepositoryCustom {
    // JpaSpecificationExecutor gives us findAll(Specification, Pageable)
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String search, String search1, Pageable pageable);
}