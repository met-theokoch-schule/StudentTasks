
package com.example.studenttask.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
public class Submission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_task_id", nullable = false)
    private UserTask userTask;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_content_id", nullable = false)
    private TaskContent taskContent; // Reference to submitted content version
    
    @Column(nullable = false)
    private LocalDateTime submittedAt;
    
    @Column(nullable = false)
    private Integer version; // Reference to TaskContent.version

    // Constructors
    public Submission() {
        this.submittedAt = LocalDateTime.now();
    }
    
    public Submission(UserTask userTask, TaskContent taskContent) {
        this();
        this.userTask = userTask;
        this.taskContent = taskContent;
        this.version = taskContent.getVersion();
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
    
    public TaskContent getTaskContent() {
        return taskContent;
    }
    
    public void setTaskContent(TaskContent taskContent) {
        this.taskContent = taskContent;
        if (taskContent != null) {
            this.version = taskContent.getVersion();
        }
    }
    
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
    
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Submission that = (Submission) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "Submission{" +
                "id=" + id +
                ", version=" + version +
                ", submittedAt=" + submittedAt +
                '}';
    }
}
