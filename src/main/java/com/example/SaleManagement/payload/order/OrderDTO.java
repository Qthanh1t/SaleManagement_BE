package com.example.SaleManagement.payload.order;

import com.example.SaleManagement.model.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderDTO {
    private Long id;
    private Instant orderDate;
    private BigDecimal totalAmount;
    private String status;

    // Thông tin lồng nhau
    private Long customerId;
    private String customerName;
    private String customerPhone;

    private Long userId; // Nhân viên
    private String userName; // Tên nhân viên

    private List<OrderDetailDTO> orderDetails;

    // Mapper
    public static OrderDTO fromEntity(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getFullName())
                .customerPhone(order.getCustomer().getPhoneNumber())
                .userId(order.getUser().getId())
                .userName(order.getUser().getFullName())
                .orderDetails(
                        order.getOrderDetails().stream()
                                .map(OrderDetailDTO::fromEntity)
                                .collect(Collectors.toList())
                )
                .build();
    }
}