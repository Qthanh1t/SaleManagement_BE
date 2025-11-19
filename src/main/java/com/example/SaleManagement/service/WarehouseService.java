package com.example.SaleManagement.service;

import com.example.SaleManagement.exception.ResourceNotFoundException;
import com.example.SaleManagement.model.*;
import com.example.SaleManagement.payload.warehouse.AdjustmentRequest;
import com.example.SaleManagement.payload.warehouse.CreateReceiptRequest;
import com.example.SaleManagement.payload.warehouse.ReceiptItemRequest;
import com.example.SaleManagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WarehouseService {

    @Autowired private GoodsReceiptRepository receiptRepository;
    @Autowired private StockAdjustmentRepository adjustmentRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private UserRepository userRepository;

    // Helper để lấy User hiện tại
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow();
    }

    // --- 1. NHẬP KHO (TĂNG TỒN KHO) ---
    @Transactional
    public GoodsReceipt createReceipt(CreateReceiptRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));

        GoodsReceipt receipt = new GoodsReceipt();
        receipt.setSupplier(supplier);
        receipt.setUser(getCurrentUser());
        receipt.setNote(request.getNote());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (ReceiptItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.getProductId()));

            // 🔥 Tăng tồn kho (Cộng dồn)
            Inventory inventory = inventoryRepository.findByIdWithPessimisticLock(item.getProductId())
                    .orElseThrow();
            inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
            inventoryRepository.save(inventory);

            // Tạo chi tiết phiếu
            GoodsReceiptDetail detail = new GoodsReceiptDetail();
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setEntryPrice(item.getEntryPrice());

            receipt.addDetail(detail);

            // Tính tổng tiền
            totalAmount = totalAmount.add(item.getEntryPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        receipt.setTotalAmount(totalAmount);
        return receiptRepository.save(receipt);
    }

    // --- 2. KIỂM KHO (ĐIỀU CHỈNH TỒN KHO) ---
    @Transactional
    public StockAdjustment adjustStock(AdjustmentRequest request) {
        // 🔥 Khóa row inventory
        Inventory inventory = inventoryRepository.findByIdWithPessimisticLock(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", request.getProductId()));

        int oldQty = inventory.getQuantity();
        int newQty = request.getNewQuantity();

        // Cập nhật số lượng mới
        inventory.setQuantity(newQty);
        inventoryRepository.save(inventory);

        // Lưu lịch sử
        StockAdjustment adjustment = new StockAdjustment();
        adjustment.setProduct(inventory.getProduct());
        adjustment.setUser(getCurrentUser());
        adjustment.setOldQuantity(oldQty);
        adjustment.setNewQuantity(newQty);
        adjustment.setReason(request.getReason());

        return adjustmentRepository.save(adjustment);
    }
}