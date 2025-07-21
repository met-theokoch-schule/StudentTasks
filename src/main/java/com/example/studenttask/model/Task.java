package com.example.studenttask.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description; // Markdown-formatted task description

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime dueDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "task_groups",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<Group> assignedGroups = new HashSet<>();

    @Column(nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "view_type_id", referencedColumnName = "id")
    private TaskView viewType; // References to available task views

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String defaultSubmission; // Default content for submissions in the format expected by the task view

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_view_id")
    private TaskView taskView;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private Set<UserTask> userTasks = new HashSet<>();

    // Constructors
    public Task() {
        this.createdAt = LocalDateTime.now();
    }

    public Task(String title, String description, User createdBy, TaskView viewType) {
        this();
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.viewType = viewType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setTaskView(TaskView taskView) {
        this.taskView = taskView;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Set<Group> getAssignedGroups() {
        return assignedGroups;
    }

    public void setAssignedGroups(Set<Group> assignedGroups) {
        this.assignedGroups = assignedGroups;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setActive(Boolean active) {
        this.isActive = active;
    }

    public TaskView getViewType() {
        return this.viewType;
    }

    public void setViewType(TaskView viewType) {
        this.viewType = viewType;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDefaultSubmission() {
        return defaultSubmission;
    }

    public void setDefaultSubmission(String defaultSubmission) {
        this.defaultSubmission = defaultSubmission;
    }

    public TaskView getTaskView() {
        return taskView;
    }

    public Set<UserTask> getUserTasks() {
        return userTasks;
    }

    public void setUserTasks(Set<UserTask> userTasks) {
        this.userTasks = userTasks;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id != null ? id.equals(task.id) : task.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", createdAt=" + createdAt +
                ", dueDate=" + dueDate +
                ", isActive=" + isActive +
                '}';
    }
}