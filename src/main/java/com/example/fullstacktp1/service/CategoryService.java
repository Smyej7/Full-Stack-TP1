package com.example.fullstacktp1.service;

import com.example.fullstacktp1.dto.CategoryDTO;
import com.example.fullstacktp1.mapper.CategoryMapper;
import com.example.fullstacktp1.model.Category;
import com.example.fullstacktp1.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    // Création d'une catégorie
    public Category createCategory(Category category) {
        if (category.getParent() != null && category.getParent().getId().equals(category.getId())) {
            throw new IllegalArgumentException("Une catégorie ne peut pas être son propre parent.");
        }
        category.setCreationDate(new Date());
        return categoryRepository.save(category);
    }

    // Lecture paginée et triée
    public List<Category> getAllCategories(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return categoryRepository.findAll(pageable).getContent();
    }

    // Lecture par ID
    public Optional<Category> getCategoryById(Long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        return optionalCategory;
    }

    // Mise à jour d'une catégorie
    public Category updateCategory(Long id, Category updatedCategory) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (updatedCategory.getParent() != null) {
            categoryRepository.findById(updatedCategory.getParent().getId())
                    .orElseThrow(() -> new RuntimeException("Parent Category not found"));

            if (updatedCategory.getParent().getId().equals(id)) {
                throw new IllegalArgumentException("Une catégorie ne peut pas être son propre parent.");
            }

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
                category.getParent(),
                category.getParent() == null,
                category.getChildren()
        ));
    }
}
