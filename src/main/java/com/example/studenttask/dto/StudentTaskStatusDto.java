package com.example.studenttask.dto;

import com.example.studenttask.model.TaskStatus;

public class StudentTaskStatusDto {
    private TaskStatus status;
    private boolean hasSubmissions;
    private Long userTaskId;
    private String statusIcon;
    private String statusColor;

    public StudentTaskStatusDto() {
    }

    public StudentTaskStatusDto(TaskStatus status, boolean hasSubmissions, Long userTaskId,
                                String statusIcon, String statusColor) {
        this.status = status;
        this.hasSubmissions = hasSubmissions;
        this.userTaskId = userTaskId;
        this.statusIcon = statusIcon;
        this.statusColor = statusColor;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public boolean isHasSubmissions() {
        return hasSubmissions;
    }

    public void setHasSubmissions(boolean hasSubmissions) {
        this.hasSubmissions = hasSubmissions;
    }

    public Long getUserTaskId() {
        return userTaskId;
    }

    public void setUserTaskId(Long userTaskId) {
        this.userTaskId = userTaskId;
    }

    public String getStatusIcon() {
        return statusIcon;
    }

    public void setStatusIcon(String statusIcon) {
        this.statusIcon = statusIcon;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }
}
