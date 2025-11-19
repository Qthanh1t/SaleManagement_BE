package com.example.SaleManagement.payload.warehouse;

import lombok.Data;
import java.util.List;

@Data
public class CreateReceiptRequest {
    private Long supplierId;
    private String note;
    private List<ReceiptItemRequest> items;
}