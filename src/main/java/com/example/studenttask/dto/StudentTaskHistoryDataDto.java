package com.example.studenttask.dto;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.UserTask;

import java.util.List;

public class StudentTaskHistoryDataDto {
    private Task task;
    private UserTask userTask;
    private List<TaskContent> contentVersions;
    private List<TaskReview> reviews;

    public StudentTaskHistoryDataDto() {
    }

    public StudentTaskHistoryDataDto(
            Task task,
            UserTask userTask,
            List<TaskContent> contentVersions,
            List<TaskReview> reviews) {
        this.task = task;
        this.userTask = userTask;
        this.contentVersions = contentVersions;
        this.reviews = reviews;
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

    public List<TaskContent> getContentVersions() {
        return contentVersions;
    }

    public void setContentVersions(List<TaskContent> contentVersions) {
        this.contentVersions = contentVersions;
    }

    public List<TaskReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<TaskReview> reviews) {
        this.reviews = reviews;
    }
}
