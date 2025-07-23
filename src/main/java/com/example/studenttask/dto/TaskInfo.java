package com.example.studenttask.dto;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatus;

public class TaskInfo {
    private Long taskId;
    private String taskTitle;
    private TaskStatus status;
    private Long userTaskId;
    private Task task;
    private boolean hasSubmissions;

    // Constructors
    public TaskInfo() {}

    public TaskInfo(Long taskId, String taskTitle, TaskStatus status, Long userTaskId) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.status = status;
        this.userTaskId = userTaskId;
    }

    public TaskInfo(Long userTaskId, Task task, TaskStatus status, boolean hasSubmissions) {
        this.userTaskId = userTaskId;
        this.task = task;
        this.status = status;
        this.hasSubmissions = hasSubmissions;
        if (task != null) {
            this.taskId = task.getId();
            this.taskTitle = task.getTitle();
        }
    }

    // Getters and Setters
    public Long getTaskId() {
        return taskId != null ? taskId : (task != null ? task.getId() : null);
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle != null ? taskTitle : (task != null ? task.getTitle() : null);
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
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
        if (task != null) {
            this.taskId = task.getId();
            this.taskTitle = task.getTitle();
        }
    }

    public boolean isHasSubmissions() {
        return hasSubmissions;
    }

    public void setHasSubmissions(boolean hasSubmissions) {
        this.hasSubmissions = hasSubmissions;
    }
}