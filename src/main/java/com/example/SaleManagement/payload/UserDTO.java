package com.example.SaleManagement.payload;

import com.example.SaleManagement.model.User;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String roleName;
    private Long roleId;

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRoleName(user.getRole().getName());
        dto.setRoleId(user.getRole().getId());
        return dto;
    }
}