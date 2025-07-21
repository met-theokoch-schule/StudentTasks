
package com.example.studenttask.repository;

import com.example.studenttask.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by name (case-insensitive)
     */
    Optional<Category> findByNameIgnoreCase(String name);
    
    /**
     * Find category by exact name
     */
    Optional<Category> findByName(String name);

    /**
     * Find all active categories ordered by name
     */
    List<Category> findByIsActiveTrueOrderByName();

    /**
     * Find all categories ordered by name
     */
    List<Category> findAllByOrderByName();

    /**
     * Check if category exists by name
     */
    boolean existsByNameIgnoreCase(String name);
}
