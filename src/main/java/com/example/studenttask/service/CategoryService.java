
package com.example.studenttask.service;

import com.example.studenttask.model.Category;
import com.example.studenttask.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Find all categories
     */
    public List<Category> findAll() {
        return categoryRepository.findAllByOrderByName();
    }

    /**
     * Find all active categories
     */
    public List<Category> findActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByName();
    }

    /**
     * Find category by ID
     */
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Find category by name
     */
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name);
    }

    /**
     * Create or update category
     */
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Create category if it doesn't exist
     */
    public Category createIfNotExists(String name, String description) {
        Optional<Category> existing = categoryRepository.findByNameIgnoreCase(name);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        Category category = new Category(name, description);
        return categoryRepository.save(category);
    }

    /**
     * Delete category
     */
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    /**
     * Check if category exists
     */
    public boolean existsByName(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    /**
     * Activate category
     */
    public void activateCategory(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            category.setIsActive(true);
            categoryRepository.save(category);
        }
    }

    /**
     * Deactivate category
     */
    public void deactivateCategory(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            category.setIsActive(false);
            categoryRepository.save(category);
        }
    }
}
