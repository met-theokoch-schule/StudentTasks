
package com.example.studenttask.dto;

import com.example.studenttask.model.User;
import java.util.List;

public class StudentTaskInfo {
    private User student;
    private List<TaskInfo> taskInfos;

    public StudentTaskInfo() {}

    public StudentTaskInfo(User student, List<TaskInfo> taskInfos) {
        this.student = student;
        this.taskInfos = taskInfos;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public List<TaskInfo> getTaskInfos() {
        return taskInfos;
    }

    public void setTaskInfos(List<TaskInfo> taskInfos) {
        this.taskInfos = taskInfos;
    }
}
