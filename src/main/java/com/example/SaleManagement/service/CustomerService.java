package com.example.SaleManagement.service;

import com.example.SaleManagement.exception.ResourceConflictException;
import com.example.SaleManagement.model.Customer;
import com.example.SaleManagement.payload.CustomerDTO;
import com.example.SaleManagement.repository.CustomerRepository;
import com.example.SaleManagement.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.SaleManagement.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderRepository orderRepository;

    private CustomerDTO toDTO(Customer c) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(c.getId());
        dto.setFullName(c.getFullName());
        dto.setPhoneNumber(c.getPhoneNumber());
        dto.setEmail(c.getEmail());
        dto.setAddress(c.getAddress());
        return dto;
    }

    public Page<CustomerDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> searchCustomersByPhone(String phone) {
        return customerRepository.findByPhoneNumberContaining(phone)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CustomerDTO createCustomer(CustomerDTO dto) {
        if (customerRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new ResourceConflictException("Số điện thoại " + dto.getPhoneNumber() + " đã tồn tại.");
        }
        Customer customer = new Customer();
        customer.setFullName(dto.getFullName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        return toDTO(customerRepository.save(customer));
    }

    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        if (customerRepository.existsByPhoneNumberAndIdNot(dto.getPhoneNumber(), id)) {
            throw new ResourceConflictException("Số điện thoại " + dto.getPhoneNumber() + " đã tồn tại.");
        }
        customer.setFullName(dto.getFullName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        return toDTO(customerRepository.save(customer));
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        if (orderRepository.existsByCustomerId(id)) {
            throw new ResourceConflictException("Không thể xóa khách hàng đã có đơn hàng.");
        }

        customerRepository.delete(customer);
    }
}