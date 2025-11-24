package com.example.SaleManagement.controller;

import com.example.SaleManagement.model.Order;
import com.example.SaleManagement.payload.order.OrderCreateRequest;
import com.example.SaleManagement.payload.order.OrderDTO;
import com.example.SaleManagement.service.OrderService;
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

    // Lấy danh sách (Cả Admin và Sales)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_STAFF')")
    public Page<OrderDTO> getAllOrders(@PageableDefault(size = 10, sort = "orderDate") Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    // Lấy chi tiết (Cả Admin và Sales)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_STAFF')")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_STAFF')")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Hủy đơn hàng thành công. Đã hoàn kho.");
    }
}