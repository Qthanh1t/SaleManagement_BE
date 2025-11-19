package com.example.SaleManagement.controller;

import com.example.SaleManagement.payload.warehouse.AdjustmentRequest;
import com.example.SaleManagement.payload.warehouse.CreateReceiptRequest;
import com.example.SaleManagement.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouse")
@PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF')")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @PostMapping("/receipts")
    public ResponseEntity<?> createReceipt(@RequestBody CreateReceiptRequest request) {
        warehouseService.createReceipt(request);
        return ResponseEntity.ok("Nhập kho thành công");
    }

    @PostMapping("/adjustments")
    public ResponseEntity<?> adjustStock(@RequestBody AdjustmentRequest request) {
        warehouseService.adjustStock(request);
        return ResponseEntity.ok("Điều chỉnh kho thành công");
    }
}