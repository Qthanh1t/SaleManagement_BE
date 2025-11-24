package com.example.SaleManagement.service;

import com.example.SaleManagement.payload.dashboard.DashboardStatsDTO;
import com.example.SaleManagement.payload.dashboard.TopProductDTO;
import com.example.SaleManagement.repository.CustomerRepository;
import com.example.SaleManagement.repository.OrderDetailRepository;
import com.example.SaleManagement.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public DashboardStatsDTO getDashboardStats(LocalDate startDate, LocalDate endDate) {

        // Chuyển LocalDate -> Instant (UTC)
        // Start: 00:00:00 của ngày bắt đầu
        Instant startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);

        // End: 23:59:59.999999999 của ngày kết thúc
        Instant endInstant = endDate.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);

        // 1. Doanh thu
        BigDecimal revenue = orderRepository.findTotalRevenueBetween(startInstant, endInstant);

        // 2. Số đơn
        Long orders = orderRepository.countOrdersBetween(startInstant, endInstant);

        // 3. Khách mới
        Long newCustomers = customerRepository.countNewCustomersBetween(startInstant, endInstant);

        // 4. Top sản phẩm
        List<TopProductDTO> topProducts = orderDetailRepository.findTopSellingProductsBetween(
                startInstant, endInstant, PageRequest.of(0, 5));

        return DashboardStatsDTO.builder()
                .totalRevenueToday(revenue) // Tên field DTO hơi cũ (Today), nhưng ta cứ tái sử dụng
                .totalOrdersToday(orders)
                .newCustomersToday(newCustomers)
                .topSellingProducts(topProducts)
                .build();
    }
}