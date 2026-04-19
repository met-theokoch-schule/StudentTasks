package com.example.studenttask.dto;

import com.example.studenttask.model.TaskContent;

public class TaskContentCommandResultDto {

    private final ApiOperationStatus status;
    private final TaskContent taskContent;

    private TaskContentCommandResultDto(ApiOperationStatus status, TaskContent taskContent) {
        this.status = status;
        this.taskContent = taskContent;
    }

    public static TaskContentCommandResultDto success(TaskContent taskContent) {
        return new TaskContentCommandResultDto(ApiOperationStatus.SUCCESS, taskContent);
    }

    public static TaskContentCommandResultDto success() {
        return new TaskContentCommandResultDto(ApiOperationStatus.SUCCESS, null);
    }

    public static TaskContentCommandResultDto unauthorized() {
        return new TaskContentCommandResultDto(ApiOperationStatus.UNAUTHORIZED, null);
    }

    public static TaskContentCommandResultDto notFound() {
        return new TaskContentCommandResultDto(ApiOperationStatus.NOT_FOUND, null);
    }

    public ApiOperationStatus getStatus() {
        return status;
    }

    public TaskContent getTaskContent() {
        return taskContent;
    }
}
