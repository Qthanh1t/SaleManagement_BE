package com.example.SaleManagement.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierDTO {
    private Long id;

    @NotBlank(message = "Tên không được để trống")
    private String name;

    private String contactPerson;
    private String email;
    private String phoneNumber;
    private String address;
}