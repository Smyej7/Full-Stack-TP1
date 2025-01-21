package com.example.fullstacktp1.controller;

import com.example.fullstacktp1.dto.CategoryDTO;
import com.example.fullstacktp1.model.Category;
import com.example.fullstacktp1.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Création d'une nouvelle catégorie
    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    // Lecture de toutes les catégories avec pagination
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(categoryService.getAllCategories(page, size, sortBy));
    }

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
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Mise à jour d'une catégorie
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody @Valid Category category) {
        return ResponseEntity.ok(categoryService.updateCategory(id, category));
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
