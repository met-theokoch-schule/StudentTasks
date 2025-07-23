
package com.example.studenttask.dto;

public class GroupStatistics {
    private int totalStudents;
    private int activeTasks;
    private int pendingSubmissions;
    private int completedSubmissions;

    // Constructors
    public GroupStatistics() {}

    public GroupStatistics(int totalStudents, int activeTasks, int pendingSubmissions, int completedSubmissions) {
        this.totalStudents = totalStudents;
        this.activeTasks = activeTasks;
        this.pendingSubmissions = pendingSubmissions;
        this.completedSubmissions = completedSubmissions;
    }

    // Getters and Setters
    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getActiveTasks() {
        return activeTasks;
    }

    public void setActiveTasks(int activeTasks) {
        this.activeTasks = activeTasks;
    }

    public int getPendingSubmissions() {
        return pendingSubmissions;
    }

    public void setPendingSubmissions(int pendingSubmissions) {
        this.pendingSubmissions = pendingSubmissions;
    }

    public int getCompletedSubmissions() {
        return completedSubmissions;
    }

    public void setCompletedSubmissions(int completedSubmissions) {
        this.completedSubmissions = completedSubmissions;
    }
}
