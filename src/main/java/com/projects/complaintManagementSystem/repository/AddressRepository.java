package com.projects.complaintManagementSystem.repository;

import com.projects.complaintManagementSystem.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}