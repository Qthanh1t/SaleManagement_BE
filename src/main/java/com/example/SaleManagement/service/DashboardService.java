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

    public DashboardStatsDTO getDashboardStats() {

        // Lấy thời điểm 00:00:00 của ngày hôm nay
        Instant startOfToday = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);

        // 1. Lấy thống kê từ OrderRepo
        BigDecimal totalRevenueToday = orderRepository.findTotalRevenueSince(startOfToday);
        Long totalOrdersToday = orderRepository.countOrdersSince(startOfToday);

        // 2. Lấy thống kê từ CustomerRepo
        Long newCustomersToday = customerRepository.countNewCustomersSince(startOfToday);

        // 3. Lấy top 5 sản phẩm từ OrderDetailRepo
        List<TopProductDTO> topProducts = orderDetailRepository.findTopSellingProducts(PageRequest.of(0, 5));

        // 4. Build DTO và trả về
        return DashboardStatsDTO.builder()
                .totalRevenueToday(totalRevenueToday)
                .totalOrdersToday(totalOrdersToday)
                .newCustomersToday(newCustomersToday)
                .topSellingProducts(topProducts)
                .build();
    }
}