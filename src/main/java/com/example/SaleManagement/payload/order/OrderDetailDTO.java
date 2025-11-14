package com.example.SaleManagement.payload.order;

import com.example.SaleManagement.model.OrderDetail;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class OrderDetailDTO {
    private Long productId;
    private String productName;
    private String productSku;
    private Integer quantity;
    private BigDecimal priceAtPurchase;

    public static OrderDetailDTO fromEntity(OrderDetail od) {
        return OrderDetailDTO.builder()
                .productId(od.getProduct().getId())
                .productName(od.getProduct().getName())
                .productSku(od.getProduct().getSku())
                .quantity(od.getQuantity())
                .priceAtPurchase(od.getPriceAtPurchase())
                .build();
    }
}