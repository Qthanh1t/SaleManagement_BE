package com.example.SaleManagement.payload.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrderCreateRequest {
    @NotNull
    private Long customerId;

    @NotEmpty
    @Valid // Phải có @Valid để validate các item bên trong
    private List<OrderItemRequest> items;
}