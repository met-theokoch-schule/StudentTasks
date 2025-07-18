
package com.example.studenttask.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "task_statuses")
public class TaskStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name; // NICHT_BEGONNEN, IN_BEARBEITUNG, ABGEGEBEN, ÜBERARBEITUNG_NÖTIG, VOLLSTÄNDIG
    
    private String description;
    
    @Column(name = "status_order")
    private Integer order;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
    private Set<UserTask> userTasks = new HashSet<>();
    
    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
    private Set<TaskReview> taskReviews = new HashSet<>();
    
    // Constructors
    public TaskStatus() {}
    
    public TaskStatus(String name, String description, Integer order) {
        this.name = name;
        this.description = description;
        this.order = order;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getOrder() {
        return order;
    }
    
    public void setOrder(Integer order) {
        this.order = order;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Set<UserTask> getUserTasks() {
        return userTasks;
    }
    
    public void setUserTasks(Set<UserTask> userTasks) {
        this.userTasks = userTasks;
    }
    
    public Set<TaskReview> getTaskReviews() {
        return taskReviews;
    }
    
    public void setTaskReviews(Set<TaskReview> taskReviews) {
        this.taskReviews = taskReviews;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TaskStatus that = (TaskStatus) obj;
        return name != null ? name.equals(that.name) : that.name == null;
    }
    
    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "TaskStatus{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", order=" + order +
                ", isActive=" + isActive +
                '}';
    }
}
