package com.example.SaleManagement.payload.ai;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ExtractedInvoiceDTO {
    private String supplierName;
    private String invoiceDate; // Dạng YYYY-MM-DD
    private BigDecimal totalAmount;
    private List<ExtractedItemDTO> items;

    @Data
    public static class ExtractedItemDTO {
        private String productName;
        private Integer quantity;
        private BigDecimal price; // Giá nhập
    }
}