package com.example.SaleManagement.controller;

import com.example.SaleManagement.payload.ai.ExtractedInvoiceDTO;
import com.example.SaleManagement.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/ai")
public class AIController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/scan-invoice")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF')")
    public ResponseEntity<ExtractedInvoiceDTO> scanInvoice(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(geminiService.extractInvoiceData(file));
    }
}