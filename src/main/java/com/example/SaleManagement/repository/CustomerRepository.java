package com.example.SaleManagement.repository;

import com.example.SaleManagement.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Tìm theo SĐT (dùng cho autocomplete)
    List<Customer> findByPhoneNumberContaining(String phoneNumber);
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdAt >= :startDate")
    Long countNewCustomersSince(@Param("startDate") Instant startDate);
}