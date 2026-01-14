package com.projects.ecommerce.repository;

import com.projects.ecommerce.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Page<Feedback> findByProductId(Long productId, Pageable pageable);
    Page<Feedback> findByRating(int rating, Pageable pageable);
    Page<Feedback> findByProductIdAndRating(Long productId, int rating, Pageable pageable);
}