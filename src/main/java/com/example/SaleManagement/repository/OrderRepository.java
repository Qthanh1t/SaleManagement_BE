package com.example.SaleManagement.repository;

import com.example.SaleManagement.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Lấy tổng doanh thu trong 1 khoảng thời gian (ví dụ: hôm nay)
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.orderDate >= :startDate")
    BigDecimal findTotalRevenueSince(@Param("startDate") Instant startDate);

    // Đếm tổng số đơn hàng trong 1 khoảng thời gian
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :startDate")
    Long countOrdersSince(@Param("startDate") Instant startDate);
}