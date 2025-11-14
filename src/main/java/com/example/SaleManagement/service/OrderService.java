package com.example.SaleManagement.service;

import com.example.SaleManagement.exception.InsufficientStockException;
import com.example.SaleManagement.exception.ResourceNotFoundException;
import com.example.SaleManagement.model.*;
import com.example.SaleManagement.payload.order.OrderCreateRequest;
import com.example.SaleManagement.payload.order.OrderDTO;
import com.example.SaleManagement.payload.order.OrderItemRequest;
import com.example.SaleManagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Transactional
    public Order createOrder(OrderCreateRequest request) {
        // 1. Lấy thông tin Customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        // 2. Lấy thông tin User (nhân viên) đang đăng nhập
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        // 3. Tạo Order header
        Order order = new Order();
        order.setCustomer(customer);
        order.setUser(currentUser);
        order.setStatus("COMPLETED"); // Giả định

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 4. Xử lý từng Order Item (quan trọng nhất)
        for (OrderItemRequest itemRequest : request.getItems()) {

            // 4a. Lấy thông tin Product
            Long productId = itemRequest.getProductId();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

            // 4b. 🔥 KHÓA VÀ KIỂM TRA TỒN KHO
            // Dùng hàm findByIdWithPessimisticLock đã tạo
            Inventory inventory = inventoryRepository.findByIdWithPessimisticLock(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));

            int requestedQuantity = itemRequest.getQuantity();
            int currentStock = inventory.getQuantity();

            if (currentStock < requestedQuantity) {
                // Nếu không đủ, ném Exception -> Transactional sẽ tự động ROLLBACK
                throw new InsufficientStockException("Không đủ tồn kho cho sản phẩm: " + product.getName()
                        + ". (Chỉ còn " + currentStock + ")");
            }

            // 4c. TRỪ KHO
            inventory.setQuantity(currentStock - requestedQuantity);
            inventoryRepository.save(inventory); // Lưu lại số lượng tồn kho mới

            // 4d. Tạo OrderDetail (dòng hàng)
            OrderDetail detail = new OrderDetail();
            detail.setProduct(product);
            detail.setQuantity(requestedQuantity);
            detail.setPriceAtPurchase(product.getPrice()); // Ghi lại giá tại thời điểm mua

            order.addOrderDetail(detail); // Thêm vào Order cha

            // 4e. Tính tổng tiền
            totalAmount = totalAmount.add(
                    product.getPrice().multiply(BigDecimal.valueOf(requestedQuantity))
            );
        }

        // 5. Set tổng tiền và Lưu Order (header)
        order.setTotalAmount(totalAmount);

        // Nhờ `cascade = CascadeType.ALL` trên Order,
        // khi save Order, các OrderDetail cũng tự động được save.
        return orderRepository.save(order);
    }

    // Lấy danh sách (có join fetch để tránh N+1)
    @Transactional(readOnly = true) // Giao dịch chỉ đọc
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        // Cần join fetch để lấy customer và user (tránh N+1 query)
        Page<Order> orderPage = orderRepository.findAllWithCustomerAndUser(pageable);
        return orderPage.map(OrderDTO::fromEntity); // Dùng map của Page
    }

    // Lấy chi tiết 1 đơn
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return OrderDTO.fromEntity(order);
    }
}