package com.example.SaleManagement.service;

import com.example.SaleManagement.exception.ResourceConflictException;
import com.example.SaleManagement.exception.ResourceNotFoundException;
import com.example.SaleManagement.model.Category;
import com.example.SaleManagement.payload.CategoryDTO;
import com.example.SaleManagement.repository.CategoryRepository;
import com.example.SaleManagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    // --- Helper (Mapper) ---
    private CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    private Category toEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }
    // --- End Helper ---

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = toEntity(categoryDTO);
        return toDTO(categoryRepository.save(category));
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        return toDTO(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if(productRepository.existsByCategoryId(id)) {
            throw new ResourceConflictException("Không thể xóa danh mục đang chứa sản phẩm");
        }

        categoryRepository.delete(category);
    }
}