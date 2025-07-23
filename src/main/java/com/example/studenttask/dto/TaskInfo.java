package com.example.studenttask.dto;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatus;

public class TaskInfo {
    private Long userTaskId;
    private Task task;
    private TaskStatus status;
    private boolean hasSubmissions;

    public TaskInfo() {}

    public TaskInfo(Long userTaskId, Task task, TaskStatus status, boolean hasSubmissions) {
        this.userTaskId = userTaskId;
        this.task = task;
        this.status = status;
        this.hasSubmissions = hasSubmissions;
    }

    public Long getUserTaskId() {
        return userTaskId;
    }

    public void setUserTaskId(Long userTaskId) {
        this.userTaskId = userTaskId;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Long getTaskId() {
        return task != null ? task.getId() : null;
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
}