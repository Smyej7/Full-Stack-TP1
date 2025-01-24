package com.example.fullstacktp1.controller;

import com.example.fullstacktp1.dto.CategoryDTO;
import com.example.fullstacktp1.mapper.CategoryMapper;
import com.example.fullstacktp1.model.Category;
import com.example.fullstacktp1.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Créer une nouvelle catégorie", description = "Ajoute une nouvelle catégorie au système.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie créée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(
            @Parameter(description = "Détails de la catégorie à créer") @Valid @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(categoryMapper.toDTO(savedCategory));
    }

    // Lecture de toutes les catégories avec pagination
    @Operation(summary = "Récupérer toutes les catégories", description = "Retourne une liste paginée de toutes les catégories.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des catégories récupérée avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories(
            @Parameter(description = "Numéro de la page à récupérer") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ utilisé pour le tri") @RequestParam(defaultValue = "id") String sortBy) {
        List<Category> categories = categoryService.getAllCategories(page, size, sortBy);
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }

    // Lecture des catégories triées
    @Operation(summary = "Récupérer les catégories triées", description = "Retourne une liste paginée et triée de catégories.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste triée des catégories récupérée avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @GetMapping("/sort")
    public ResponseEntity<Page<CategoryDTO>> getSortedCategories(
            @Parameter(description = "Champ utilisé pour le tri") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direction du tri (asc ou desc)") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Numéro de la page à récupérer") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page") @RequestParam(defaultValue = "10") int size) {
        Page<CategoryDTO> categories = categoryService.getSortedCategories(sortBy, direction, page, size);
        return ResponseEntity.ok(categories);
    }

    // Lecture d'une catégorie par ID
    @Operation(summary = "Récupérer une catégorie par ID", description = "Retourne les détails d'une catégorie spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie récupérée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(
            @Parameter(description = "ID de la catégorie à récupérer") @PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(value -> ResponseEntity.ok(categoryMapper.toDTO(value)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Mise à jour d'une catégorie
    @Operation(summary = "Mettre à jour une catégorie", description = "Met à jour les détails d'une catégorie existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie mise à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @Parameter(description = "ID de la catégorie à mettre à jour") @PathVariable Long id,
            @Parameter(description = "Détails de la catégorie à mettre à jour") @Valid @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        Category updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(categoryMapper.toDTO(updatedCategory));
    }

    // Suppression d'une catégorie
    @Operation(summary = "Supprimer une catégorie", description = "Supprime une catégorie existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(
            @Parameter(description = "ID de la catégorie à supprimer") @PathVariable Long id) {
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
