package com.example.fullstacktp1.repository;

import com.example.fullstacktp1.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}