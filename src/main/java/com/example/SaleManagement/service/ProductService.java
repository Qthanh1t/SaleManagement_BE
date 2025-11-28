package com.example.SaleManagement.service;

import com.example.SaleManagement.exception.ResourceConflictException;
import com.example.SaleManagement.exception.ResourceNotFoundException;
import com.example.SaleManagement.model.Category;
import com.example.SaleManagement.model.Inventory;
import com.example.SaleManagement.model.Product;
import com.example.SaleManagement.payload.ProductDTO;
import com.example.SaleManagement.payload.ProductRequest;
import com.example.SaleManagement.repository.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private GoodsReceiptDetailRepository goodsReceiptDetailRepository;

    // --- Helper (Mapper) ---
    private ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .stockQuantity(product.getInventory() != null ? product.getInventory().getQuantity() : 0)
                .isActive(product.getIsActive())
                .imageUrl(product.getImageUrl())
                .build();
    }

    // --- Specification (Search) ---
    // Hàm này tạo 1 query động để tìm theo tên hoặc sku
    public static Specification<Product> searchByKeyword(String keyword, Long categoryId) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";

                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), likePattern),
                        cb.like(cb.lower(root.get("sku")), likePattern)
                ));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            query.distinct(true);
            // Lưu ý: Chỉ fetch nếu query là select (để tránh lỗi khi count)
            if (Long.class != query.getResultType()) {
                root.fetch("category");
                root.fetch("inventory");
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    // --- End Specification ---


    public Page<ProductDTO> getAllProducts(String search, Pageable pageable, Long categoryId) {
        Specification<Product> spec = searchByKeyword(search, categoryId);
        return productRepository.findAll(spec, pageable).map(this::toDTO);
    }

    @Transactional
    public List<ProductDTO> searchActiveProducts(String keyword) {
        if (keyword == null) keyword = "";

        // Giới hạn chỉ lấy 10 kết quả tốt nhất để hiển thị trên dropdown
        Pageable limit = PageRequest.of(0, 10);

        return productRepository.searchActiveProducts(keyword, limit)
                .stream()
                .map(this::toDTO) // Tái sử dụng hàm toDTO có sẵn
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO createProduct(ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequest.getCategoryId()));

        // Tạo Product
        Product product = new Product();
        product.setSku(productRequest.getSku());
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(category);
        product.setImageUrl(productRequest.getImageUrl());

        // Tạo Inventory
        Inventory inventory = new Inventory();
        inventory.setQuantity(productRequest.getInitialStock());

        // Liên kết 2 chiều
        product.setInventory(inventory);

        Product savedProduct = productRepository.save(product);
        return toDTO(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequest.getCategoryId()));

        product.setSku(productRequest.getSku());
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(category);
        product.setImageUrl(productRequest.getImageUrl());

        // Cập nhật inventory
        //Inventory inventory = product.getInventory();
        //inventory.setQuantity(productRequest.getInitialStock());

        Product updatedProduct = productRepository.save(product);
        return toDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        // CHECK 1: Đã từng bán chưa?
        if (orderDetailRepository.existsByProductId(id)) {
            throw new ResourceConflictException("Không thể xóa sản phẩm đã có lịch sử đơn hàng.");
        }

        // CHECK 2: Đã từng nhập kho chưa?
        if (goodsReceiptDetailRepository.existsByProductId(id)) {
            throw new ResourceConflictException("Không thể xóa sản phẩm đã có lịch sử nhập kho.");
        }
        productRepository.delete(product);
    }

    @Transactional
    public List<ProductDTO> getLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void toggleProductStatus(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Đảo ngược trạng thái: Nếu đang Active -> Inactive và ngược lại
        // Điều này giúp bạn có thể khôi phục nhân viên cũ nếu họ quay lại làm việc
        product.setIsActive(!product.getIsActive());

        productRepository.save(product);
    }
}