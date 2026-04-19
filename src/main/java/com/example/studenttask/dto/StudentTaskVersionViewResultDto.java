package com.example.studenttask.dto;

public class StudentTaskVersionViewResultDto {
    private StudentTaskViewDataDto viewData;
    private String redirectPath;

    public StudentTaskVersionViewResultDto() {
    }

    public StudentTaskVersionViewResultDto(StudentTaskViewDataDto viewData, String redirectPath) {
        this.viewData = viewData;
        this.redirectPath = redirectPath;
    }

    public static StudentTaskVersionViewResultDto view(StudentTaskViewDataDto viewData) {
        return new StudentTaskVersionViewResultDto(viewData, null);
    }

    public static StudentTaskVersionViewResultDto redirect(String redirectPath) {
        return new StudentTaskVersionViewResultDto(null, redirectPath);
    }

    public StudentTaskViewDataDto getViewData() {
        return viewData;
    }

    public void setViewData(StudentTaskViewDataDto viewData) {
        this.viewData = viewData;
    }

    public String getRedirectPath() {
        return redirectPath;
    }

    public void setRedirectPath(String redirectPath) {
        this.redirectPath = redirectPath;
    }

    public boolean isRedirect() {
        return redirectPath != null;
    }
}
