package com.example.SaleManagement.repository;

import com.example.SaleManagement.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}