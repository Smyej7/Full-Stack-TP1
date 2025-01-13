package com.example.fullstacktp1.controller;

import com.example.fullstacktp1.model.Category;
import com.example.fullstacktp1.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // Lecture de toutes les catégories avec pagination et tri
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(categoryService.getAllCategories(page, size, sortBy));
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
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(id, category));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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

    // Recherche avec filtres
    @GetMapping("/search")
    public ResponseEntity<Page<Object>> getCategoriesWithPagination(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categories = categoryService.getCategoriesWithPagination(pageable);

        Page<Object> response = categories.map(category -> {
            return new Object() {
                public final String name = category.getName();
                public final Date creationDate = category.getCreationDate();
                public final Set<String> children = category.getChildren().stream().map(Category::getName).collect(Collectors.toSet());
                public final boolean isRoot = category.isRoot();
            };
        });

        return ResponseEntity.ok(response);
    }
}
