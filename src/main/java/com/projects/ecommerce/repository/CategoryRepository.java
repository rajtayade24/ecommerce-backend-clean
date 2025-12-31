package com.projects.ecommerce.repository;


import com.projects.ecommerce.entity.Category;
import com.projects.ecommerce.repository.custom.CategoryRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> , CategoryRepositoryCustom {
    Optional<Category> findBySlug(String slug);
    boolean existsBySlug(String slug);
    boolean existsByName(String name);

    Page<Category> findByNameContainingIgnoreCase(String search, Pageable pageable);
}