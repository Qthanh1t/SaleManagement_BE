package com.example.SaleManagement.service;

import com.example.SaleManagement.exception.ResourceNotFoundException;
import com.example.SaleManagement.model.Supplier;
import com.example.SaleManagement.payload.SupplierDTO;
import com.example.SaleManagement.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    private SupplierDTO toDTO(Supplier s) {
        SupplierDTO dto = new SupplierDTO();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setContactPerson(s.getContactPerson());
        dto.setEmail(s.getEmail());
        dto.setPhoneNumber(s.getPhoneNumber());
        dto.setAddress(s.getAddress());
        return dto;
    }

    private void updateEntity(Supplier s, SupplierDTO dto) {
        s.setName(dto.getName());
        s.setContactPerson(dto.getContactPerson());
        s.setEmail(dto.getEmail());
        s.setPhoneNumber(dto.getPhoneNumber());
        s.setAddress(dto.getAddress());
    }

    public Page<SupplierDTO> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable).map(this::toDTO);
    }

    public SupplierDTO createSupplier(SupplierDTO dto) {
        Supplier supplier = new Supplier();
        updateEntity(supplier, dto);
        return toDTO(supplierRepository.save(supplier));
    }

    public SupplierDTO updateSupplier(Long id, SupplierDTO dto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        updateEntity(supplier, dto);
        return toDTO(supplierRepository.save(supplier));
    }

    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        // (Sau này cần check xem NCC có phiếu nhập nào không)
        supplierRepository.delete(supplier);
    }
}