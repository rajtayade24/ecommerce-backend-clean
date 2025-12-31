package com.projects.ecommerce.repository;

import com.projects.ecommerce.entity.CartItem;
import com.projects.ecommerce.entity.Product;
import com.projects.ecommerce.entity.ProductVariant;
import com.projects.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findById(Long id);

    Optional<CartItem> findByUserAndVariant(User user, ProductVariant variant);

    Optional<CartItem> findByUserAndProduct(User user, Product product);

    Page<CartItem> findByUser(User user, Pageable pageable);

    long countByUser(User user);

    Optional<CartItem> findByIdAndUser(Long id, User user);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user = :user")
    void deleteByUser(@Param("user") User user);
}