package com.example.studenttask.dto;

public class GroupStatisticsDto {
    private int totalStudents;
    private int submittedTasks;
    private int needsRevisionTasks;
    private int completedTasks;

    public GroupStatisticsDto() {
    }

    public GroupStatisticsDto(int totalStudents, int submittedTasks, int needsRevisionTasks, int completedTasks) {
        this.totalStudents = totalStudents;
        this.submittedTasks = submittedTasks;
        this.needsRevisionTasks = needsRevisionTasks;
        this.completedTasks = completedTasks;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getSubmittedTasks() {
        return submittedTasks;
    }

    public void setSubmittedTasks(int submittedTasks) {
        this.submittedTasks = submittedTasks;
    }

    public int getNeedsRevisionTasks() {
        return needsRevisionTasks;
    }

    public void setNeedsRevisionTasks(int needsRevisionTasks) {
        this.needsRevisionTasks = needsRevisionTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }
}
