package com.example.SaleManagement.service;

import com.example.SaleManagement.payload.ai.ExtractedInvoiceDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExtractedInvoiceDTO extractInvoiceData(MultipartFile file) {
        try {
            // 1. Khởi tạo Client (Thư viện tự xử lý URL và Auth)
            Client client = Client.builder()
                    .apiKey(apiKey)
                    .build();

            // 2. Chuẩn bị ảnh
            byte[] imageBytes = file.getBytes();

            // 3. Chuẩn bị Prompt
            String prompt = "Bạn là một trợ lý nhập liệu. Hãy trích xuất thông tin từ hóa đơn này và trả về JSON thuần túy (không markdown, không ```json```). " +
                    "Cấu trúc JSON cần trả về: " +
                    "{ \"supplierName\": \"string\", \"invoiceDate\": \"YYYY-MM-DD\", \"totalAmount\": number, " +
                    "\"items\": [ { \"productName\": \"string\", \"quantity\": number, \"price\": number } ] } " +
                    "Nếu không tìm thấy thông tin nào, hãy để null hoặc 0.";

            // 4. Tạo nội dung gửi đi (Multimodal: Text + Image)
            List<Part> parts = new ArrayList<>();

            // Phần Text
            parts.add(Part.builder().text(prompt).build());

            // Phần Ảnh
            parts.add(Part.builder()
                    .inlineData(com.google.genai.types.Blob.builder()
                            .mimeType("image/jpeg") // Hoặc tự động detect từ file
                            .data(imageBytes)
                            .build())
                    .build());

            Content content = Content.builder().parts(parts).build();

            // 5. Gọi API (Sử dụng model gemini-1.5-flash)
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash", // Tên model
                    content,
                    null // Config (temperature, etc.) có thể để null nếu mặc định
            );

            // 6. Lấy kết quả text
            String jsonText = response.text();

            // Làm sạch chuỗi (đề phòng AI trả về Markdown)
            if (jsonText.contains("```json")) {
                jsonText = jsonText.replace("```json", "").replace("```", "");
            }
            jsonText = jsonText.trim();

            // 7. Map sang DTO
            return objectMapper.readValue(jsonText, ExtractedInvoiceDTO.class);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi gọi Google Gemini SDK: " + e.getMessage());
        }
    }
}