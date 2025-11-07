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
}