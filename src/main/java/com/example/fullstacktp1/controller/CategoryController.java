package com.example.fullstacktp1.controller;

import com.example.fullstacktp1.dto.CategoryDTO;
import com.example.fullstacktp1.mapper.CategoryMapper;
import com.example.fullstacktp1.model.Category;
import com.example.fullstacktp1.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    // Création d'une nouvelle catégorie
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(categoryMapper.toDTO(savedCategory));
    }

    // Lecture de toutes les catégories avec pagination
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        List<Category> categories = categoryService.getAllCategories(page, size, sortBy);
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }

    // Lecture des catégories triées
    @GetMapping("/sort")
    public ResponseEntity<Page<CategoryDTO>> getSortedCategories(
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CategoryDTO> categories = categoryService.getSortedCategories(sortBy, direction, page, size);
        return ResponseEntity.ok(categories);
    }

    // Lecture d'une catégorie par ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(value -> ResponseEntity.ok(categoryMapper.toDTO(value)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Mise à jour d'une catégorie
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        Category updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(categoryMapper.toDTO(updatedCategory));
    }

    // Suppression d'une catégorie
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        if (categoryService.categoryExists(id)) {
            categoryService.deleteCategory(id);
            response.put("message", "Catégorie supprimée avec succès.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("error", "Catégorie non trouvée.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


}
