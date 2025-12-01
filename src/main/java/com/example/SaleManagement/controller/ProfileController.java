package com.example.SaleManagement.controller;

import com.example.SaleManagement.payload.ChangePasswordRequest;
import com.example.SaleManagement.payload.UpdateProfileRequest;
import com.example.SaleManagement.payload.UserDTO;
import com.example.SaleManagement.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@PreAuthorize("isAuthenticated()") // Ai đăng nhập cũng dùng được
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public ResponseEntity<UserDTO> getProfile() {
        return ResponseEntity.ok(profileService.getProfile());
    }

    @PutMapping("/info")
    public ResponseEntity<UserDTO> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        profileService.changePassword(request);
        return ResponseEntity.ok("Đổi mật khẩu thành công.");
    }
}