package com.example.studenttask.dto;

import com.example.studenttask.model.Task;

public class TeacherTaskListItemDto {
    private final Task task;
    private final boolean hasSubmissions;
    private final boolean hasPendingReviews;

    public TeacherTaskListItemDto(Task task, boolean hasSubmissions, boolean hasPendingReviews) {
        this.task = task;
        this.hasSubmissions = hasSubmissions;
        this.hasPendingReviews = hasPendingReviews;
    }

    public Task getTask() {
        return task;
    }

    public boolean isHasSubmissions() {
        return hasSubmissions;
    }

    public boolean isHasPendingReviews() {
        return hasPendingReviews;
    }
}
