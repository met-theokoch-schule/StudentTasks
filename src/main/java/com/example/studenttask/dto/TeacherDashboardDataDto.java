package com.example.studenttask.dto;

import com.example.studenttask.model.Task;

import java.util.List;

public class TeacherDashboardDataDto {
    private int pendingReviews;
    private List<Task> recentTasks;

    public TeacherDashboardDataDto() {
    }

    public TeacherDashboardDataDto(int pendingReviews, List<Task> recentTasks) {
        this.pendingReviews = pendingReviews;
        this.recentTasks = recentTasks;
    }

    public int getPendingReviews() {
        return pendingReviews;
    }

    public void setPendingReviews(int pendingReviews) {
        this.pendingReviews = pendingReviews;
    }

    public List<Task> getRecentTasks() {
        return recentTasks;
    }

    public void setRecentTasks(List<Task> recentTasks) {
        this.recentTasks = recentTasks;
    }
}
