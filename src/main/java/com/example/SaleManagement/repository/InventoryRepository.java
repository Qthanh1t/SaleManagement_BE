package com.example.SaleManagement.repository;

import com.example.SaleManagement.model.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // Hàm này sẽ khóa (lock) row inventory lại khi đọc
    // để ngăn chặn 2 giao dịch (transaction) cùng đọc và cùng trừ kho
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.id = :productId")
    Optional<Inventory> findByIdWithPessimisticLock(Long productId);
}