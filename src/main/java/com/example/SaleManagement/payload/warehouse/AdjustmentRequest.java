package com.example.SaleManagement.payload.warehouse;

import lombok.Data;

@Data
public class AdjustmentRequest {
    private Long productId;
    private Integer newQuantity;
    private String reason;
}