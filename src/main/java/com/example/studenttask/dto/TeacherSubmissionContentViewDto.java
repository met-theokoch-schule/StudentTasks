package com.example.studenttask.dto;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.UserTask;

public class TeacherSubmissionContentViewDto {
    private Task task;
    private UserTask userTask;
    private String currentContent;
    private Integer version;
    private String templatePath;

    public TeacherSubmissionContentViewDto() {
    }

    public TeacherSubmissionContentViewDto(Task task, UserTask userTask, String currentContent, Integer version,
                                          String templatePath) {
        this.task = task;
        this.userTask = userTask;
        this.currentContent = currentContent;
        this.version = version;
        this.templatePath = templatePath;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public UserTask getUserTask() {
        return userTask;
    }

    public void setUserTask(UserTask userTask) {
        this.userTask = userTask;
    }

    public String getCurrentContent() {
        return currentContent;
    }

    public void setCurrentContent(String currentContent) {
        this.currentContent = currentContent;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }
}
