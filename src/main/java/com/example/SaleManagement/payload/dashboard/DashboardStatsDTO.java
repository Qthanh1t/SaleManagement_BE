package com.example.SaleManagement.payload.dashboard;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardStatsDTO {
    private BigDecimal totalRevenueToday;
    private Long totalOrdersToday;
    private Long newCustomersToday; // (Bonus)
    private List<TopProductDTO> topSellingProducts; // Top 5
}