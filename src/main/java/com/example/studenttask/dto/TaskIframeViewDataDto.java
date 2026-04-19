package com.example.studenttask.dto;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.UserTask;

public class TaskIframeViewDataDto {
    private Task task;
    private TaskView taskView;
    private UserTask userTask;
    private String currentContent;
    private String renderedDescription;
    private boolean teacherView;

    public TaskIframeViewDataDto() {
    }

    public TaskIframeViewDataDto(
            Task task,
            TaskView taskView,
            UserTask userTask,
            String currentContent,
            String renderedDescription,
            boolean teacherView) {
        this.task = task;
        this.taskView = taskView;
        this.userTask = userTask;
        this.currentContent = currentContent;
        this.renderedDescription = renderedDescription;
        this.teacherView = teacherView;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TaskView getTaskView() {
        return taskView;
    }

    public void setTaskView(TaskView taskView) {
        this.taskView = taskView;
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

    public String getRenderedDescription() {
        return renderedDescription;
    }

    public void setRenderedDescription(String renderedDescription) {
        this.renderedDescription = renderedDescription;
    }

    public boolean isTeacherView() {
        return teacherView;
    }

    public void setTeacherView(boolean teacherView) {
        this.teacherView = teacherView;
    }
}
