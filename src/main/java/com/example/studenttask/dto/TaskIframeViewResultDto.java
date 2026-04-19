package com.example.studenttask.dto;

public class TaskIframeViewResultDto {
    private TaskIframeViewDataDto viewData;
    private String redirectPath;

    public TaskIframeViewResultDto() {
    }

    public TaskIframeViewResultDto(TaskIframeViewDataDto viewData, String redirectPath) {
        this.viewData = viewData;
        this.redirectPath = redirectPath;
    }

    public static TaskIframeViewResultDto view(TaskIframeViewDataDto viewData) {
        return new TaskIframeViewResultDto(viewData, null);
    }

    public static TaskIframeViewResultDto redirect(String redirectPath) {
        return new TaskIframeViewResultDto(null, redirectPath);
    }

    public TaskIframeViewDataDto getViewData() {
        return viewData;
    }

    public void setViewData(TaskIframeViewDataDto viewData) {
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
