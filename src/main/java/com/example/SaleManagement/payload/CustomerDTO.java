package com.example.SaleManagement.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerDTO {
    private Long id;

    @NotBlank
    private String fullName;

    @NotBlank
    private String phoneNumber;

    private String email;
    private String address;
}