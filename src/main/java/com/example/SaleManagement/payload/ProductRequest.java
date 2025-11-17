package com.example.SaleManagement.payload;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "SKU không được để trống")
    private String sku;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;

    @NotNull(message = "Tồn kho không được để trống")
    @Min(value = 0, message = "Tồn kho phải lớn hơn hoặc bằng 0")
    private Integer initialStock;

    private String imageUrl;
}