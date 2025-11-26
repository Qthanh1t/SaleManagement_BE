package com.example.SaleManagement.controller;

import com.example.SaleManagement.payload.CustomerDTO;
import com.example.SaleManagement.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@PreAuthorize("hasAnyRole('ADMIN', 'SALES_STAFF')") // Bảo vệ toàn bộ controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public Page<CustomerDTO> getAllCustomers(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @PageableDefault(size = 10) Pageable pageable) {
        return customerService.getAllCustomers(search, pageable);
    }

    // API để tìm kiếm (autocomplete)
    @GetMapping("/search")
    public List<CustomerDTO> searchCustomers(@RequestParam String phone) {
        return customerService.searchCustomersByPhone(phone);
    }

    // API để tạo nhanh khách hàng
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        return new ResponseEntity<>(customerService.createCustomer(customerDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Xóa khách hàng thành công");
    }
}