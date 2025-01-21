package com.example.fullstacktp1.service;

import com.example.fullstacktp1.dto.CategoryDTO;
import com.example.fullstacktp1.model.Category;
import com.example.fullstacktp1.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Création d'une catégorie
    public Category createCategory(Category category) {
        if (category.getParent() != null && category.getParent().getId().equals(category.getId())) {
            throw new IllegalArgumentException("Une catégorie ne peut pas être son propre parent.");
        }
        return categoryRepository.save(category);
    }

    // Lecture paginée et triée
    public List<Category> getAllCategories(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return categoryRepository.findAll(pageable).getContent();
    }

    // Lecture par ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // Mise à jour d'une catégorie
    public Category updateCategory(Long id, Category updatedCategory) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (updatedCategory.getParent() != null) {
            // Vérification : Empêcher l'ajout d'un parent avec un id inexistant
            categoryRepository.findById(updatedCategory.getParent().getId())
                    .orElseThrow(() -> new RuntimeException("Parent Category not found"));

            // Vérification : Une catégorie ne peut pas être son propre parent
            if (updatedCategory.getParent().getId().equals(id)) {
                throw new IllegalArgumentException("Une catégorie ne peut pas être son propre parent.");
            }

            // Vérification : Empêcher une boucle hiérarchique
            Long parentId = updatedCategory.getParent().getId();
            if (isCircularReference(id, parentId)) {
                throw new IllegalArgumentException("Une catégorie ne peut pas créer une boucle hiérarchique avec ses parents.");
            }
        }

        category.setName(updatedCategory.getName());
        category.setParent(updatedCategory.getParent());
        return categoryRepository.save(category);
    }

    private boolean isCircularReference(Long categoryId, Long parentId) {
        Category parentCategory = categoryRepository.findById(parentId)
                .orElse(null);
        while (parentCategory != null) {
            if (parentCategory.getId().equals(categoryId)) {
                return true;
            }
            parentCategory = parentCategory.getParent();
        }
        return false;
    }


    // Suppression d'une catégorie
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public boolean categoryExists(Long id) {
        return categoryRepository.existsById(id);
    }

    public Page<CategoryDTO> getSortedCategories(String sortBy, String direction, int page, int size) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return categoryRepository.findAll(pageable).map(category -> new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getCreationDate(),
                category.getParent() == null,
                category.getChildren()
        ));
    }
}
