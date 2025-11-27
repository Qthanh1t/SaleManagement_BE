package com.example.SaleManagement.repository;

import com.example.SaleManagement.model.StockAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Integer> {
    boolean existsByUserId(Long userId);
}
