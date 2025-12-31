package com.projects.ecommerce.entity;

import com.projects.ecommerce.enums.SuperCoinReason;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "supercoin_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuperCoinTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // positive for credit, negative for debit
    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SuperCoinReason reason;

    private String referenceId; // orderId, refundId, etc.

    private OffsetDateTime createdAt = OffsetDateTime.now();
}