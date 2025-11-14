package com.example.SaleManagement.controller;

import com.example.SaleManagement.payload.dashboard.DashboardStatsDTO;
import com.example.SaleManagement.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được xem
    public DashboardStatsDTO getStats() {
        return dashboardService.getDashboardStats();
    }
}