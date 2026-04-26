package com.example.studenttask.dto;

import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.UnitTitle;

import java.util.List;

public class TeacherTaskFormDataDto {
    private Task task;
    private TeacherTaskFormDto taskForm;
    private List<TaskView> taskViews;
    private List<Group> groups;
    private List<UnitTitle> unitTitles;

    public TeacherTaskFormDataDto() {
    }

    public TeacherTaskFormDataDto(Task task, TeacherTaskFormDto taskForm, List<TaskView> taskViews, List<Group> groups,
            List<UnitTitle> unitTitles) {
        this.task = task;
        this.taskForm = taskForm;
        this.taskViews = taskViews;
        this.groups = groups;
        this.unitTitles = unitTitles;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TeacherTaskFormDto getTaskForm() {
        return taskForm;
    }

    public void setTaskForm(TeacherTaskFormDto taskForm) {
        this.taskForm = taskForm;
    }

    public List<TaskView> getTaskViews() {
        return taskViews;
    }

    public void setTaskViews(List<TaskView> taskViews) {
        this.taskViews = taskViews;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<UnitTitle> getUnitTitles() {
        return unitTitles;
    }

    public void setUnitTitles(List<UnitTitle> unitTitles) {
        this.unitTitles = unitTitles;
    }
}
