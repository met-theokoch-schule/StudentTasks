package com.example.studenttask.dto;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatus;

public class TaskInfo {
    private Long taskId;
    private String taskTitle;
    private TaskStatus status;
    private Long userTaskId;

    // Constructors
    public TaskInfo() {}

    public TaskInfo(Long taskId, String taskTitle, TaskStatus status, Long userTaskId) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.status = status;
        this.userTaskId = userTaskId;
    }

    // Getters and Setters
    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
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
}
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