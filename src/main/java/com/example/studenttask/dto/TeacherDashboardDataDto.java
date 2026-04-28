package com.example.studenttask.dto;

import com.example.studenttask.model.Task;

import java.util.List;

public class TeacherDashboardDataDto {
    private int pendingReviews;
    private List<Task> recentTasks;
    private boolean showReviewReminder;
    private String reviewReminderMessage;

    public TeacherDashboardDataDto() {
    }

    public TeacherDashboardDataDto(
        int pendingReviews,
        List<Task> recentTasks,
        boolean showReviewReminder,
        String reviewReminderMessage
    ) {
        this.pendingReviews = pendingReviews;
        this.recentTasks = recentTasks;
        this.showReviewReminder = showReviewReminder;
        this.reviewReminderMessage = reviewReminderMessage;
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

    public boolean isShowReviewReminder() {
        return showReviewReminder;
    }

    public void setShowReviewReminder(boolean showReviewReminder) {
        this.showReviewReminder = showReviewReminder;
    }

    public String getReviewReminderMessage() {
        return reviewReminderMessage;
    }

    public void setReviewReminderMessage(String reviewReminderMessage) {
        this.reviewReminderMessage = reviewReminderMessage;
    }
}
