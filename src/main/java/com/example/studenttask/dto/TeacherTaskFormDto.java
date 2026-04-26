package com.example.studenttask.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TeacherTaskFormDto {

    @NotBlank(message = "Bitte geben Sie einen Aufgabentitel ein.")
    private String title;
    private String description;
    private String tutorial;
    private String defaultSubmission;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dueDate;

    private Boolean isActive = true;
    private Long taskViewId;
    private String unitTitleId;
    private List<Long> selectedGroups = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTutorial() {
        return tutorial;
    }

    public void setTutorial(String tutorial) {
        this.tutorial = tutorial;
    }

    public String getDefaultSubmission() {
        return defaultSubmission;
    }

    public void setDefaultSubmission(String defaultSubmission) {
        this.defaultSubmission = defaultSubmission;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Long getTaskViewId() {
        return taskViewId;
    }

    public void setTaskViewId(Long taskViewId) {
        this.taskViewId = taskViewId;
    }

    public String getUnitTitleId() {
        return unitTitleId;
    }

    public void setUnitTitleId(String unitTitleId) {
        this.unitTitleId = unitTitleId;
    }

    public List<Long> getSelectedGroups() {
        return selectedGroups;
    }

    public void setSelectedGroups(List<Long> selectedGroups) {
        this.selectedGroups = selectedGroups == null ? new ArrayList<>() : new ArrayList<>(selectedGroups);
    }
}
