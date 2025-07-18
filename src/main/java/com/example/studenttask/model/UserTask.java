
package com.example.studenttask.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_tasks")
public class UserTask {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private TaskStatus status;
    
    private LocalDateTime startedAt;
    
    @Column(nullable = false)
    private LocalDateTime lastModified;
    
    @OneToMany(mappedBy = "userTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("version DESC")
    private List<TaskContent> contents = new ArrayList<>();
    
    @OneToMany(mappedBy = "userTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("submittedAt DESC")
    private List<Submission> submissions = new ArrayList<>();
    
    @OneToMany(mappedBy = "userTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("reviewedAt DESC")
    private List<TaskReview> reviews = new ArrayList<>();
    
    // Constructors
    public UserTask() {
        this.lastModified = LocalDateTime.now();
    }
    
    public UserTask(User user, Task task, TaskStatus status) {
        this();
        this.user = user;
        this.task = task;
        this.status = status;
        this.startedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Task getTask() {
        return task;
    }
    
    public void setTask(Task task) {
        this.task = task;
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getLastModified() {
        return lastModified;
    }
    
    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }
    
    public List<TaskContent> getContents() {
        return contents;
    }
    
    public void setContents(List<TaskContent> contents) {
        this.contents = contents;
    }
    
    public List<Submission> getSubmissions() {
        return submissions;
    }
    
    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }
    
    public List<TaskReview> getReviews() {
        return reviews;
    }
    
    public void setReviews(List<TaskReview> reviews) {
        this.reviews = reviews;
    }
    
    // Helper methods
    public TaskContent getLatestContent() {
        return contents.isEmpty() ? null : contents.get(0);
    }
    
    public Submission getLatestSubmission() {
        return submissions.isEmpty() ? null : submissions.get(0);
    }
    
    public TaskReview getLatestReview() {
        return reviews.isEmpty() ? null : reviews.get(0);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserTask userTask = (UserTask) obj;
        return id != null ? id.equals(userTask.id) : userTask.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "UserTask{" +
                "id=" + id +
                ", user=" + (user != null ? user.getName() : "null") +
                ", task=" + (task != null ? task.getTitle() : "null") +
                ", status=" + (status != null ? status.getName() : "null") +
                ", lastModified=" + lastModified +
                '}';
    }
}
