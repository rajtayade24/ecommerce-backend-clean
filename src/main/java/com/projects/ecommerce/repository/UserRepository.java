package com.projects.ecommerce.repository;

import com.projects.ecommerce.entity.User;
import com.projects.ecommerce.enums.AuthProviderType;
import com.projects.ecommerce.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmailOrMobile(String email, String mobile);

    long countByRolesContainingAndActive(RoleType roleType, boolean b);

    Optional<Object> findByEmail(String normalized);

    Optional<User> findByProviderIdAndProviderType(String providerId, AuthProviderType providerType);
}
