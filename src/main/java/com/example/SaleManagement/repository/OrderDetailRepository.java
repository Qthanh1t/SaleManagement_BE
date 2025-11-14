package com.example.SaleManagement.repository;


import com.example.SaleManagement.model.OrderDetail;
import com.example.SaleManagement.payload.dashboard.TopProductDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    // Thống kê top sản phẩm bán chạy
    @Query("SELECT new com.example.SaleManagement.payload.dashboard.TopProductDTO(od.product.id, od.product.name, SUM(od.quantity)) " +
            "FROM OrderDetail od " +
            "GROUP BY od.product.id, od.product.name " +
            "ORDER BY SUM(od.quantity) DESC")
    List<TopProductDTO> findTopSellingProducts(Pageable pageable);
}