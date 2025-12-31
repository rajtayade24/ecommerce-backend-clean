package com.projects.ecommerce.repository;

import com.projects.ecommerce.entity.SuperCoinTransaction;
import com.projects.ecommerce.enums.SuperCoinReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SuperCoinTransactionRepository extends JpaRepository<SuperCoinTransaction, Long> {

 
    boolean existsByUserIdAndReasonAndReferenceId(
            Long id,
            SuperCoinReason superCoinReason,
            String string
    );

    @Query("SELECT COALESCE(SUM(t.amount),0) FROM SuperCoinTransaction t WHERE t.user.id = :userId")
    int getUserBalance(@Param("userId") Long userId);
}



