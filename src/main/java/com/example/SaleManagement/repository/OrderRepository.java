package com.example.SaleManagement.repository;

import com.example.SaleManagement.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Lấy tổng doanh thu trong 1 khoảng thời gian (ví dụ: hôm nay)
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.orderDate >= :startDate")
    BigDecimal findTotalRevenueSince(@Param("startDate") Instant startDate);

    // Đếm tổng số đơn hàng trong 1 khoảng thời gian
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :startDate")
    Long countOrdersSince(@Param("startDate") Instant startDate);

    Boolean existsByCustomerId(Long customerId);

    // Lấy Page, join 2 bảng customer và user
    @Query(value = "SELECT o FROM Order o JOIN FETCH o.customer c JOIN FETCH o.user u",
            countQuery = "SELECT count(o) FROM Order o")
    Page<Order> findAllWithCustomerAndUser(Pageable pageable);

    // Lấy 1 Order, join tất cả các bảng liên quan
    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.customer " +
            "JOIN FETCH o.user " +
            "JOIN FETCH o.orderDetails od " +
            "JOIN FETCH od.product " +
            "WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);
}