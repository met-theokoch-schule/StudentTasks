package com.example.studenttask.dto;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.UserTask;

public class StudentTaskViewDataDto {
    private Task task;
    private UserTask userTask;
    private TaskView taskView;
    private String currentContent;
    private Integer viewingVersion;
    private boolean historyView;

    public StudentTaskViewDataDto() {
    }

    public StudentTaskViewDataDto(
            Task task,
            UserTask userTask,
            TaskView taskView,
            String currentContent,
            Integer viewingVersion,
            boolean historyView) {
        this.task = task;
        this.userTask = userTask;
        this.taskView = taskView;
        this.currentContent = currentContent;
        this.viewingVersion = viewingVersion;
        this.historyView = historyView;
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

    public TaskView getTaskView() {
        return taskView;
    }

    public void setTaskView(TaskView taskView) {
        this.taskView = taskView;
    }

    public String getCurrentContent() {
        return currentContent;
    }

    public void setCurrentContent(String currentContent) {
        this.currentContent = currentContent;
    }

    public Integer getViewingVersion() {
        return viewingVersion;
    }

    public void setViewingVersion(Integer viewingVersion) {
        this.viewingVersion = viewingVersion;
    }

    public boolean isHistoryView() {
        return historyView;
    }

    public void setHistoryView(boolean historyView) {
        this.historyView = historyView;
    }
}
