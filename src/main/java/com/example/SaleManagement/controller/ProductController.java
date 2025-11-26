package com.example.SaleManagement.controller;

import com.example.SaleManagement.payload.ProductDTO;
import com.example.SaleManagement.payload.ProductRequest;
import com.example.SaleManagement.service.ProductService;
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
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Ai cũng thấy được (Sales, Admin, Warehouse)
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<ProductDTO> getAllProducts(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable) {
        return productService.getAllProducts(search, pageable, categoryId);
    }

    // Chỉ Admin được tạo
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        return new ResponseEntity<>(productService.createProduct(productRequest), HttpStatus.CREATED);
    }

    // Chỉ Admin được sửa
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productService.updateProduct(id, productRequest));
    }

    // Chỉ Admin được xóa
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Xóa sản phẩm thành công");
    }

    @GetMapping("/low-stock")
    @PreAuthorize("isAuthenticated()") // Ai đăng nhập cũng xem được
    public List<ProductDTO> getLowStockProducts(@RequestParam(defaultValue = "5") int threshold) {
        return productService.getLowStockProducts(threshold);
    }
}