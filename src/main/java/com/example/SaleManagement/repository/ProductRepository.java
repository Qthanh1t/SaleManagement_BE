package com.example.SaleManagement.repository;

import com.example.SaleManagement.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// Thêm JpaSpecificationExecutor để tìm kiếm động
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // Lấy Product kèm số lượng tồn kho
    @Query("SELECT p, i.quantity FROM Product p JOIN p.inventory i WHERE p.id = :id")
    Object findProductWithInventory(Long id);

    // Tối ưu hóa việc load (EAGER) category và inventory khi tìm kiếm
    @Query(value = "SELECT p FROM Product p JOIN FETCH p.category JOIN FETCH p.inventory",
            countQuery = "SELECT count(p) FROM Product p")
    Page<Product> findAllWithDetails(Pageable pageable);
    boolean existsByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p JOIN p.inventory i WHERE i.quantity <= :threshold")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);
}