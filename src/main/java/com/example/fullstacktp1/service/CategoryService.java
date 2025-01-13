package com.example.fullstacktp1.service;

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
        return categoryRepository.findById(id).map(category -> {
            if (updatedCategory.getParent() != null && updatedCategory.getParent().getId().equals(id)) {
                throw new IllegalArgumentException("Une catégorie ne peut pas être son propre parent.");
            }
            category.setName(updatedCategory.getName());
            category.setParent(updatedCategory.getParent());
            return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    // Suppression d'une catégorie
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // Recherche avec filtres
    public List<Category> searchCategories(Boolean isRoot, String afterDate, String beforeDate) {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .filter(category -> isRoot == null || (isRoot && category.getParent() == null) || (!isRoot && category.getParent() != null))
                .filter(category -> {
                    if (afterDate != null) {
                        Date after = Date.valueOf(afterDate);
                        return category.getCreationDate().after(after);
                    }
                    return true;
                })
                .filter(category -> {
                    if (beforeDate != null) {
                        Date before = Date.valueOf(beforeDate);
                        return category.getCreationDate().before(before);
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public boolean categoryExists(Long id) {
        return categoryRepository.existsById(id);
    }

    public Page<Category> getCategoriesWithPagination(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
}
