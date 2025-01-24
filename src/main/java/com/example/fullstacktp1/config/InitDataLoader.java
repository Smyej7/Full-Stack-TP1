package com.example.fullstacktp1.config;

import com.example.fullstacktp1.model.Category;
import com.example.fullstacktp1.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitDataLoader {

    @Bean
    public CommandLineRunner loadTestData(CategoryRepository categoryRepository) {
        return args -> {
            // Catégories racines
            Category root1 = new Category();
            root1.setName("Catégorie a");
            categoryRepository.save(root1);

            Category root2 = new Category();
            root2.setName("Catégorie b");
            categoryRepository.save(root2);

            // Sous-catégories
            Category child1 = new Category();
            child1.setName("Catégorie c");
            child1.setParent(root1);
            categoryRepository.save(child1);

            Category child2 = new Category();
            child2.setName("Catégorie d");
            child2.setParent(root1);
            categoryRepository.save(child2);
        };
    }
}