package com.example.SaleManagement.service;

import com.example.SaleManagement.exception.ResourceNotFoundException;
import com.example.SaleManagement.model.Category;
import com.example.SaleManagement.model.Inventory;
import com.example.SaleManagement.model.Product;
import com.example.SaleManagement.payload.ProductDTO;
import com.example.SaleManagement.payload.ProductRequest;
import com.example.SaleManagement.repository.CategoryRepository;
import com.example.SaleManagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

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
                .imageUrl(product.getImageUrl())
                .build();
    }

    // --- Specification (Search) ---
    // Hàm này tạo 1 query động để tìm theo tên hoặc sku
    public static Specification<Product> searchByKeyword(String keyword) {
        return (root, query, cb) -> {
            // (Chỉ fetch nếu là query đếm số lượng, tránh lỗi cú pháp)
            if (Long.class != query.getResultType()) {
                root.fetch("category");
                root.fetch("inventory");
                query.distinct(true); // Thêm distinct để tránh trùng lặp khi join
            }
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            // Join fetch để lấy luôn category và inventory
//            query.distinct(true);
//            root.fetch("category");
//            root.fetch("inventory");

            return cb.or(
                    cb.like(cb.lower(root.get("name")), likePattern),
                    cb.like(cb.lower(root.get("sku")), likePattern)
            );
        };
    }
    // --- End Specification ---


    public Page<ProductDTO> getAllProducts(String search, Pageable pageable) {
        Specification<Product> spec = searchByKeyword(search);
        return productRepository.findAll(spec, pageable).map(this::toDTO);
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
        Inventory inventory = product.getInventory();
        inventory.setQuantity(productRequest.getInitialStock());

        Product updatedProduct = productRepository.save(product);
        return toDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        // Cần kiểm tra xem có đơn hàng nào dùng product này không trước khi xóa
        // (Sẽ implement ở Milestone 3, giờ cứ xóa)
        productRepository.delete(product);
    }
}