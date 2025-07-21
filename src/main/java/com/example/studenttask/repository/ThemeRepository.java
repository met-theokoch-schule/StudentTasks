
package com.example.studenttask.repository;

import com.example.studenttask.model.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {
    
    Optional<Theme> findByName(String name);
    
    List<Theme> findByIsActiveOrderByName(boolean isActive);
    
    List<Theme> findAllByOrderByName();
}
