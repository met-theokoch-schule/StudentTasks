package com.example.studenttask.dto;

public class TaskContentRequestDto {

    private String content;

    public TaskContentRequestDto() {
    }

    public TaskContentRequestDto(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
