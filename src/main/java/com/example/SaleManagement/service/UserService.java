package com.example.SaleManagement.service;

import com.example.SaleManagement.exception.ResourceConflictException;
import com.example.SaleManagement.exception.ResourceNotFoundException;
import com.example.SaleManagement.payload.UserCreateRequest;
import com.example.SaleManagement.model.Role;
import com.example.SaleManagement.model.User;
import com.example.SaleManagement.payload.UserDTO;
import com.example.SaleManagement.repository.*;
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
    @Autowired private OrderRepository orderRepository;
    @Autowired private GoodsReceiptRepository goodsReceiptRepository;
    @Autowired private StockAdjustmentRepository stockAdjustmentRepository;

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
        // CHECK 1: Có đơn hàng bán ra không?
        if (orderRepository.existsByUserId(id)) {
            throw new ResourceConflictException("Không thể xóa nhân viên đã từng tạo đơn hàng.");
        }

        // CHECK 2: Có phiếu nhập kho không?
        if (goodsReceiptRepository.existsByUserId(id)) {
            throw new ResourceConflictException("Không thể xóa nhân viên đã từng nhập kho.");
        }

        // CHECK 3: Có phiếu kiểm kho không?
        if (stockAdjustmentRepository.existsByUserId(id)) {
            throw new ResourceConflictException("Không thể xóa nhân viên đã từng kiểm kê kho.");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Đảo ngược trạng thái: Nếu đang Active -> Inactive và ngược lại
        // Điều này giúp bạn có thể khôi phục nhân viên cũ nếu họ quay lại làm việc
        user.setIsActive(!user.getIsActive());

        userRepository.save(user);
    }
}