
package com.example.studenttask.service;

import com.example.studenttask.model.Theme;
import com.example.studenttask.repository.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThemeService {

    @Autowired
    private ThemeRepository themeRepository;

    /**
     * Get all themes
     */
    public List<Theme> findAllThemes() {
        return themeRepository.findAllByOrderByName();
    }

    /**
     * Get all active themes
     */
    public List<Theme> findActiveThemes() {
        return themeRepository.findByIsActiveOrderByName(true);
    }

    /**
     * Find theme by ID
     */
    public Optional<Theme> findById(Long id) {
        return themeRepository.findById(id);
    }

    /**
     * Find theme by name
     */
    public Optional<Theme> findByName(String name) {
        return themeRepository.findByName(name);
    }

    /**
     * Create or update theme
     */
    public Theme saveTheme(Theme theme) {
        return themeRepository.save(theme);
    }

    /**
     * Create a new theme
     */
    public Theme createTheme(String name, String description) {
        Theme theme = new Theme(name, description);
        return themeRepository.save(theme);
    }

    /**
     * Find or create theme by name
     */
    public Theme findOrCreateTheme(String name, String description) {
        Optional<Theme> existingTheme = findByName(name);
        if (existingTheme.isPresent()) {
            return existingTheme.get();
        } else {
            return createTheme(name, description);
        }
    }
}
