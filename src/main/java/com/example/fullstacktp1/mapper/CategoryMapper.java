package com.example.fullstacktp1.mapper;

import com.example.fullstacktp1.dto.CategoryDTO;
import com.example.fullstacktp1.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDTO toDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getCreationDate(),
                category.getParent(),
                category.getParent() == null,
                category.getChildren()
        );
    }

    // Convertir un CategoryDTO en entité Category
    public Category toEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setCreationDate(dto.getCreationDate());
        category.setParent(dto.getParent());
        category.setChildren(dto.getChildren());
        return category;
    }
}
