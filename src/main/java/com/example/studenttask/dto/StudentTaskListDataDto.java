package com.example.studenttask.dto;

import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.UserTask;

import java.util.List;
import java.util.Map;

public class StudentTaskListDataDto {
    private List<UserTask> userTasks;
    private Map<UnitTitle, List<UserTask>> tasksByUnitTitle;

    public StudentTaskListDataDto() {
    }

    public StudentTaskListDataDto(List<UserTask> userTasks, Map<UnitTitle, List<UserTask>> tasksByUnitTitle) {
        this.userTasks = userTasks;
        this.tasksByUnitTitle = tasksByUnitTitle;
    }

    public List<UserTask> getUserTasks() {
        return userTasks;
    }

    public void setUserTasks(List<UserTask> userTasks) {
        this.userTasks = userTasks;
    }

    public Map<UnitTitle, List<UserTask>> getTasksByUnitTitle() {
        return tasksByUnitTitle;
    }

    public void setTasksByUnitTitle(Map<UnitTitle, List<UserTask>> tasksByUnitTitle) {
        this.tasksByUnitTitle = tasksByUnitTitle;
    }
}
