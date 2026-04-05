 package com.example.complaintManagementSystem.repository;

 import com.example.complaintManagementSystem.entity.Address;
 import org.springframework.data.jpa.repository.JpaRepository;

 public interface AddressRepository extends JpaRepository<Address, Long> {
 }