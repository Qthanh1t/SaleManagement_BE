package com.example.SaleManagement.payload;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder // Dùng Builder Pattern để dễ tạo object
public class ProductDTO {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
    private Integer stockQuantity; // Số lượng tồn kho
}