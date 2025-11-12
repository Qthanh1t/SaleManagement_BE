package com.example.SaleManagement.controller;

import com.example.SaleManagement.model.Order;
import com.example.SaleManagement.payload.order.OrderCreateRequest;
import com.example.SaleManagement.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_STAFF')") // Chỉ Admin và Sales được tạo đơn
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        // Service sẽ ném exception nếu lỗi (hết hàng,...)
        // GlobalExceptionHandler sẽ bắt và trả về 400
        Order order = orderService.createOrder(request);
        return new ResponseEntity<>(order.getId(), HttpStatus.CREATED); // Chỉ cần trả về ID
    }

    // (Thêm API GET /orders và GET /orders/{id} nếu cần)
}