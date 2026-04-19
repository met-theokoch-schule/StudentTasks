package com.example.studenttask.dto;

public class TaskContentLoadResultDto {

    private final ApiOperationStatus status;
    private final String content;

    private TaskContentLoadResultDto(ApiOperationStatus status, String content) {
        this.status = status;
        this.content = content;
    }

    public static TaskContentLoadResultDto success(String content) {
        return new TaskContentLoadResultDto(ApiOperationStatus.SUCCESS, content);
    }

    public static TaskContentLoadResultDto unauthorized() {
        return new TaskContentLoadResultDto(ApiOperationStatus.UNAUTHORIZED, "");
    }

    public static TaskContentLoadResultDto notFound() {
        return new TaskContentLoadResultDto(ApiOperationStatus.NOT_FOUND, "");
    }

    public ApiOperationStatus getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }
}
