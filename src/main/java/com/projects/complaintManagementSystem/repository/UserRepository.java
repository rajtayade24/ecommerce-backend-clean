package com.projects.complaintManagementSystem.repository;

import com.projects.complaintManagementSystem.entity.User;
import com.projects.complaintManagementSystem.enums.AccountStatusType;
import com.projects.complaintManagementSystem.enums.AuthProviderType;
import com.projects.complaintManagementSystem.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {


    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);

    Optional<User> findByEmailOrMobile(String email, String mobile);

    Optional<Object> findByEmail(String normalized);

    @Modifying
    @Query("DELETE FROM User u WHERE u.accountStatusType = :status AND u.createdAt < :time")
    void deleteUnverifiedUsersOlderThan(
            @Param("status") AccountStatusType status,
            @Param("time") LocalDateTime time
    );

    Optional<User> findByProviderIdAndProviderType(String providerId, AuthProviderType providerType);

    @Query("""
              SELECT u FROM User u
              LEFT JOIN FETCH u.addresses
              WHERE u.id = :id
            """)
    Optional<User> findByIdWithAddresses(Long id);

    Optional<User> findByMobile(String mobile);
}
