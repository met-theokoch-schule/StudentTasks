package com.example.studenttask.dto;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.User;

import java.util.List;
import java.util.Map;

public class StudentTaskMatrixDto {
    private List<User> students;
    private List<Task> tasks;
    private Map<String, StudentTaskStatusDto> statusMap;

    public StudentTaskMatrixDto() {
    }

    public StudentTaskMatrixDto(List<User> students, List<Task> tasks, Map<String, StudentTaskStatusDto> statusMap) {
        this.students = students;
        this.tasks = tasks;
        this.statusMap = statusMap;
    }

    public static String statusKey(Long studentId, Long taskId) {
        return studentId + "_" + taskId;
    }

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Map<String, StudentTaskStatusDto> getStatusMap() {
        return statusMap;
    }

    public void setStatusMap(Map<String, StudentTaskStatusDto> statusMap) {
        this.statusMap = statusMap;
    }

    public StudentTaskStatusDto getStatus(Long studentId, Long taskId) {
        return statusMap.get(statusKey(studentId, taskId));
    }
}
