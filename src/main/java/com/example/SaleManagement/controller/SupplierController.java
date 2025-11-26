package com.example.SaleManagement.controller;

import com.example.SaleManagement.payload.SupplierDTO;
import com.example.SaleManagement.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF')") // Chỉ Admin được quản lý NCC
    @GetMapping
    public Page<SupplierDTO> getAllSuppliers(@PageableDefault(size = 10) Pageable pageable) {
        return supplierService.getAllSuppliers(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được quản lý NCC
    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody SupplierDTO dto) {
        return new ResponseEntity<>(supplierService.createSupplier(dto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được quản lý NCC
    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierDTO dto) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được quản lý NCC
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok("Xóa nhà cung cấp thành công");
    }
}