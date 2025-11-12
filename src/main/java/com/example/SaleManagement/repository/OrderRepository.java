package com.example.SaleManagement.repository;

import com.example.SaleManagement.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}