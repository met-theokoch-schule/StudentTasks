
package com.example.studenttask.dto;

import com.example.studenttask.model.User;
import java.util.List;

public class StudentTaskData {
    private User student;
    private List<TaskInfo> taskInfos;

    // Constructors
    public StudentTaskData() {}

    public StudentTaskData(User student, List<TaskInfo> taskInfos) {
        this.student = student;
        this.taskInfos = taskInfos;
    }

    // Getters and Setters
    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public List<TaskInfo> getTaskInfos() {
        return taskInfos;
    }

    public void setTaskInfos(List<TaskInfo> taskInfos) {
        this.taskInfos = taskInfos;
    }

    // Helper method to get task info by task ID
    public TaskInfo getTaskInfo(Long taskId) {
        if (taskInfos == null) return null;
        return taskInfos.stream()
            .filter(ti -> ti.getTaskId().equals(taskId))
            .findFirst()
            .orElse(null);
    }
}
