package com.example.SaleManagement.repository;


import com.example.SaleManagement.model.OrderDetail;
import com.example.SaleManagement.payload.dashboard.TopProductDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    // Thống kê top sản phẩm bán chạy
    @Query("SELECT new com.example.SaleManagement.payload.dashboard.TopProductDTO(od.product.id, od.product.name, SUM(od.quantity)) " +
            "FROM OrderDetail od " +
            "GROUP BY od.product.id, od.product.name " +
            "ORDER BY SUM(od.quantity) DESC")
    List<TopProductDTO> findTopSellingProducts(Pageable pageable);

    @Query("SELECT new com.example.SaleManagement.payload.dashboard.TopProductDTO(od.product.id, od.product.name, SUM(od.quantity)) " +
            "FROM OrderDetail od " +
            "JOIN od.order o " + // Join để check ngày
            "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
            "AND o.status = 'COMPLETED' " +
            "GROUP BY od.product.id, od.product.name " +
            "ORDER BY SUM(od.quantity) DESC")
    List<TopProductDTO> findTopSellingProductsBetween(@Param("startDate") Instant startDate,
                                                      @Param("endDate") Instant endDate,
                                                      Pageable pageable);

    boolean existsByProductId(Long productId);
}