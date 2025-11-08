package com.example.SaleManagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "inventory")
@Getter
@Setter
public class Inventory {

    @Id
    @Column(name = "product_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Đánh dấu đây là key mapping từ OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}