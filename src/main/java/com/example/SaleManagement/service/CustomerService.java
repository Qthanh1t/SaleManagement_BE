package com.example.SaleManagement.service;

import com.example.SaleManagement.model.Customer;
import com.example.SaleManagement.payload.CustomerDTO;
import com.example.SaleManagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    private CustomerDTO toDTO(Customer c) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(c.getId());
        dto.setFullName(c.getFullName());
        dto.setPhoneNumber(c.getPhoneNumber());
        dto.setEmail(c.getEmail());
        dto.setAddress(c.getAddress());
        return dto;
    }
    @Transactional(readOnly = true)
    public List<CustomerDTO> searchCustomersByPhone(String phone) {
        return customerRepository.findByPhoneNumberContaining(phone)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CustomerDTO createCustomer(CustomerDTO dto) {
        // (Nên thêm logic kiểm tra trùng SĐT)
        Customer customer = new Customer();
        customer.setFullName(dto.getFullName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        return toDTO(customerRepository.save(customer));
    }

    // (Thêm GetById, Update, Delete nếu cần)
}