package com.example.SaleManagement.repository;

import com.example.SaleManagement.model.GoodsReceiptDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsReceiptDetailRepository extends JpaRepository<GoodsReceiptDetail, Long> {
    boolean existsByProductId(Long productId);
}
