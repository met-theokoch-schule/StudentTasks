
package com.example.studenttask.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_contents")
public class TaskContent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_task_id", nullable = false)
    private UserTask userTask;
    
    @Column(columnDefinition = "TEXT")
    private String content; // Serialized content (JSON/XML)
    
    @Column(nullable = false)
    private Integer version; // Auto-incrementing version number
    
    @Column(nullable = false)
    private LocalDateTime savedAt;
    
    @Column(nullable = false)
    private Boolean isSubmitted = false; // false = draft, true = submitted
    
    @OneToOne(mappedBy = "taskContent", cascade = CascadeType.ALL)
    private Submission submission;
    
    // Constructors
    public TaskContent() {
        this.savedAt = LocalDateTime.now();
    }
    
    public TaskContent(UserTask userTask, String content, Integer version) {
        this();
        this.userTask = userTask;
        this.content = content;
        this.version = version;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public UserTask getUserTask() {
        return userTask;
    }
    
    public void setUserTask(UserTask userTask) {
        this.userTask = userTask;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public LocalDateTime getSavedAt() {
        return savedAt;
    }
    
    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }
    
    public Boolean getIsSubmitted() {
        return isSubmitted;
    }
    
    public void setIsSubmitted(Boolean isSubmitted) {
        this.isSubmitted = isSubmitted;
    }
    
    public Submission getSubmission() {
        return submission;
    }
    
    public void setSubmission(Submission submission) {
        this.submission = submission;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TaskContent that = (TaskContent) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "TaskContent{" +
                "id=" + id +
                ", version=" + version +
                ", savedAt=" + savedAt +
                ", isSubmitted=" + isSubmitted +
                '}';
    }
}
