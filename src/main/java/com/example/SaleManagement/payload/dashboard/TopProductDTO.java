package com.example.SaleManagement.payload.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // Cần constructor này để JPQL mapping
public class TopProductDTO {
    private Long productId;
    private String productName;
    private Long totalSold; // Tổng số lượng đã bán
}