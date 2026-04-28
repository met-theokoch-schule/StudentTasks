package com.example.studenttask.dto;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.UnitTitle;

import java.util.List;
import java.util.Map;

public class TeacherTaskListDataDto {
    private List<TeacherTaskListItemDto> tasks;
    private Map<UnitTitle, List<TeacherTaskListItemDto>> tasksByUnitTitle;

    public TeacherTaskListDataDto() {
    }

    public TeacherTaskListDataDto(List<TeacherTaskListItemDto> tasks,
                                  Map<UnitTitle, List<TeacherTaskListItemDto>> tasksByUnitTitle) {
        this.tasks = tasks;
        this.tasksByUnitTitle = tasksByUnitTitle;
    }

    public List<TeacherTaskListItemDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TeacherTaskListItemDto> tasks) {
        this.tasks = tasks;
    }

    public Map<UnitTitle, List<TeacherTaskListItemDto>> getTasksByUnitTitle() {
        return tasksByUnitTitle;
    }

    public void setTasksByUnitTitle(Map<UnitTitle, List<TeacherTaskListItemDto>> tasksByUnitTitle) {
        this.tasksByUnitTitle = tasksByUnitTitle;
    }
}
