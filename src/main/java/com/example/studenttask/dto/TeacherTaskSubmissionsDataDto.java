package com.example.studenttask.dto;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.UserTask;

import java.util.List;

public class TeacherTaskSubmissionsDataDto {
    private Task task;
    private List<UserTask> userTasks;
    private boolean ownTask;

    public TeacherTaskSubmissionsDataDto() {
    }

    public TeacherTaskSubmissionsDataDto(Task task, List<UserTask> userTasks, boolean ownTask) {
        this.task = task;
        this.userTasks = userTasks;
        this.ownTask = ownTask;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<UserTask> getUserTasks() {
        return userTasks;
    }

    public void setUserTasks(List<UserTask> userTasks) {
        this.userTasks = userTasks;
    }

    public boolean isOwnTask() {
        return ownTask;
    }

    public void setOwnTask(boolean ownTask) {
        this.ownTask = ownTask;
    }
}
