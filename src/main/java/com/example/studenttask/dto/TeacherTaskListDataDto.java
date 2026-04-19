package com.example.studenttask.dto;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.UnitTitle;

import java.util.List;
import java.util.Map;

public class TeacherTaskListDataDto {
    private List<Task> tasks;
    private Map<UnitTitle, List<Task>> tasksByUnitTitle;

    public TeacherTaskListDataDto() {
    }

    public TeacherTaskListDataDto(List<Task> tasks, Map<UnitTitle, List<Task>> tasksByUnitTitle) {
        this.tasks = tasks;
        this.tasksByUnitTitle = tasksByUnitTitle;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Map<UnitTitle, List<Task>> getTasksByUnitTitle() {
        return tasksByUnitTitle;
    }

    public void setTasksByUnitTitle(Map<UnitTitle, List<Task>> tasksByUnitTitle) {
        this.tasksByUnitTitle = tasksByUnitTitle;
    }
}
