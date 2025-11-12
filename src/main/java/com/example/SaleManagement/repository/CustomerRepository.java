package com.example.SaleManagement.repository;

import com.example.SaleManagement.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Tìm theo SĐT (dùng cho autocomplete)
    List<Customer> findByPhoneNumberContaining(String phoneNumber);
    Optional<Customer> findByPhoneNumber(String phoneNumber);
}