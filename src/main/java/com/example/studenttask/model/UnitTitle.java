
package com.example.studenttask.model;

import jakarta.persistence.*;

@Entity
@Table(name = "unit_titles")
public class UnitTitle {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String description;
    
    @Column(nullable = false)
    private boolean isActive = true;
    
    // Constructors
    public UnitTitle() {}
    
    public UnitTitle(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = true;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
