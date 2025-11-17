package com.example.SaleManagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo thư mục upload!", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // 1. Tạo tên file độc nhất
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        try {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        } catch (Exception e) {
            // Bỏ qua nếu không có đuôi file
        }
        String storedFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // 2. Lưu file
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 3. Trả về URL để client truy cập
            // (Ví dụ: /uploads/abc-123.jpg)
            return "/uploads/" + storedFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Lỗi khi lưu file " + storedFileName, ex);
        }
    }
}