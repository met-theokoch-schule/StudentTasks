package com.example.studenttask.dto;

import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.UserTask;

import java.util.List;

public class TeacherSubmissionReviewDataDto {
    private UserTask userTask;
    private List<TaskReview> reviews;
    private List<TaskStatus> statuses;
    private List<VersionWithSubmissionStatus> versionsWithStatus;
    private UserTask nextReviewUserTask;

    public TeacherSubmissionReviewDataDto() {
    }

    public TeacherSubmissionReviewDataDto(UserTask userTask, List<TaskReview> reviews, List<TaskStatus> statuses,
                                          List<VersionWithSubmissionStatus> versionsWithStatus) {
        this(userTask, reviews, statuses, versionsWithStatus, null);
    }

    public TeacherSubmissionReviewDataDto(UserTask userTask, List<TaskReview> reviews, List<TaskStatus> statuses,
                                          List<VersionWithSubmissionStatus> versionsWithStatus,
                                          UserTask nextReviewUserTask) {
        this.userTask = userTask;
        this.reviews = reviews;
        this.statuses = statuses;
        this.versionsWithStatus = versionsWithStatus;
        this.nextReviewUserTask = nextReviewUserTask;
    }

    public UserTask getUserTask() {
        return userTask;
    }

    public void setUserTask(UserTask userTask) {
        this.userTask = userTask;
    }

    public List<TaskReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<TaskReview> reviews) {
        this.reviews = reviews;
    }

    public List<TaskStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<TaskStatus> statuses) {
        this.statuses = statuses;
    }

    public List<VersionWithSubmissionStatus> getVersionsWithStatus() {
        return versionsWithStatus;
    }

    public void setVersionsWithStatus(List<VersionWithSubmissionStatus> versionsWithStatus) {
        this.versionsWithStatus = versionsWithStatus;
    }

    public UserTask getNextReviewUserTask() {
        return nextReviewUserTask;
    }

    public void setNextReviewUserTask(UserTask nextReviewUserTask) {
        this.nextReviewUserTask = nextReviewUserTask;
    }
}
