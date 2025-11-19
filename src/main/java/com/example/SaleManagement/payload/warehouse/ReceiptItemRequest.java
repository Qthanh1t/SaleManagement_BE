package com.example.SaleManagement.payload.warehouse;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReceiptItemRequest {
    private Long productId;
    private Integer quantity;
    private BigDecimal entryPrice;
}