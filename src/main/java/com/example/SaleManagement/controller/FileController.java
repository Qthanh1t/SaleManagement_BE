package com.example.SaleManagement.controller;

import com.example.SaleManagement.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_STAFF')") // Chỉ user đăng nhập mới được upload
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileStorageService.storeFile(file);

        // Trả về JSON chứa URL
        // Frontend của AntD Upload cần response có dạng { "url": "..." }
        return ResponseEntity.ok(Map.of("url", fileUrl));
    }
}