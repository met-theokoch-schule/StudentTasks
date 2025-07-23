package com.example.studenttask.dto;

import java.util.Map;

public class StudentTaskData {
    private Long studentId;
    private String studentName;
    private Map<Long, String> taskStatuses;

    // Getters and Setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Map<Long, String> getTaskStatuses() {
        return taskStatuses;
    }

    public void setTaskStatuses(Map<Long, String> taskStatuses) {
        this.taskStatuses = taskStatuses;
    }
}