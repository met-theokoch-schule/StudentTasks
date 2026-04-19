package com.example.studenttask.dto;

import com.example.studenttask.model.Group;

import java.time.LocalDateTime;

public class GroupOverviewDto {
    private Group group;
    private int studentCount;
    private int activeTaskCount;
    private int pendingSubmissions;
    private LocalDateTime lastActivity;

    public GroupOverviewDto() {
    }

    public GroupOverviewDto(Group group, int studentCount, int activeTaskCount, int pendingSubmissions,
                            LocalDateTime lastActivity) {
        this.group = group;
        this.studentCount = studentCount;
        this.activeTaskCount = activeTaskCount;
        this.pendingSubmissions = pendingSubmissions;
        this.lastActivity = lastActivity;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public int getActiveTaskCount() {
        return activeTaskCount;
    }

    public void setActiveTaskCount(int activeTaskCount) {
        this.activeTaskCount = activeTaskCount;
    }

    public int getPendingSubmissions() {
        return pendingSubmissions;
    }

    public void setPendingSubmissions(int pendingSubmissions) {
        this.pendingSubmissions = pendingSubmissions;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
}
