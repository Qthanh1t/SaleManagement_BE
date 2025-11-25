package com.example.SaleManagement.controller;

import com.example.SaleManagement.model.User;
import com.example.SaleManagement.payload.LoginRequest;
import com.example.SaleManagement.payload.LoginResponse;
import com.example.SaleManagement.repository.UserRepository;
import com.example.SaleManagement.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository; // Dùng để lấy thêm thông tin

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        // Lấy thông tin user để trả về
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        String role = user.getRole().getName();

        return ResponseEntity.ok(new LoginResponse(jwt, user.getEmail(), user.getFullName(), role));
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponse> getCurrentUser() {
        // 1. Lấy email từ SecurityContext (đã được JwtAuthenticationFilter xác thực và đặt vào đây)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 2. Tìm user trong DB để lấy thông tin mới nhất (Role, FullName...)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + email));

        String role = user.getRole().getName();

        // 3. Trả về thông tin. Token để null vì Client đã có token rồi.
        return ResponseEntity.ok(new LoginResponse(null, user.getEmail(), user.getFullName(), role));
    }
}