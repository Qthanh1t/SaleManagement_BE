package com.example.SaleManagement.service;

import com.example.SaleManagement.exception.ResourceConflictException;
import com.example.SaleManagement.exception.ResourceNotFoundException;
import com.example.SaleManagement.payload.UserCreateRequest;
import com.example.SaleManagement.model.Role;
import com.example.SaleManagement.model.User;
import com.example.SaleManagement.payload.UserDTO;
import com.example.SaleManagement.repository.RoleRepository;
import com.example.SaleManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder; // Inject từ SecurityConfig

    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserDTO::fromEntity);
    }

    @Transactional
    public UserDTO createUser(UserCreateRequest request) {
        // 1. Kiểm tra email trùng
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException("Email " + request.getEmail() + " đã tồn tại.");
        }

        // 2. Lấy Role
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", request.getRoleId()));

        // 3. Tạo User
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(role);

        // 4. Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return UserDTO.fromEntity(userRepository.save(user));
    }

    // (Tùy chọn) Chức năng xóa user
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        // Nên check xem user này có đơn hàng/phiếu nhập không trước khi xóa
        // Ở đây xóa tạm thời
        userRepository.deleteById(id);
    }
}