package com.example.studenttask.dto;

import com.example.studenttask.model.UserTask;

import java.util.List;

public class StudentDashboardDataDto {
    private List<UserTask> recentUserTasks;
    private int totalTaskCount;
    private long inProgress;
    private long pendingReview;
    private long needsRework;
    private long completed;

    public StudentDashboardDataDto() {
    }

    public StudentDashboardDataDto(
            List<UserTask> recentUserTasks,
            int totalTaskCount,
            long inProgress,
            long pendingReview,
            long needsRework,
            long completed) {
        this.recentUserTasks = recentUserTasks;
        this.totalTaskCount = totalTaskCount;
        this.inProgress = inProgress;
        this.pendingReview = pendingReview;
        this.needsRework = needsRework;
        this.completed = completed;
    }

    public List<UserTask> getRecentUserTasks() {
        return recentUserTasks;
    }

    public void setRecentUserTasks(List<UserTask> recentUserTasks) {
        this.recentUserTasks = recentUserTasks;
    }

    public int getTotalTaskCount() {
        return totalTaskCount;
    }

    public void setTotalTaskCount(int totalTaskCount) {
        this.totalTaskCount = totalTaskCount;
    }

    public long getInProgress() {
        return inProgress;
    }

    public void setInProgress(long inProgress) {
        this.inProgress = inProgress;
    }

    public long getPendingReview() {
        return pendingReview;
    }

    public void setPendingReview(long pendingReview) {
        this.pendingReview = pendingReview;
    }

    public long getNeedsRework() {
        return needsRework;
    }

    public void setNeedsRework(long needsRework) {
        this.needsRework = needsRework;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }
}
