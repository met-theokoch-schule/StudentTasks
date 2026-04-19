package com.example.studenttask.service;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskStatusCode;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TaskStatusSupportTest {

    @Test
    void countByCode_groupsUserTasksByTypedStatusCodes() {
        Map<TaskStatusCode, Long> counts = TaskStatusSupport.countByCode(List.of(
                userTask("IN_BEARBEITUNG"),
                userTask("ABGEGEBEN"),
                userTask("ABGEGEBEN"),
                userTask("VOLLSTÄNDIG"),
                userTask(null)
        ));

        assertThat(counts).containsEntry(TaskStatusCode.IN_BEARBEITUNG, 1L);
        assertThat(counts).containsEntry(TaskStatusCode.ABGEGEBEN, 2L);
        assertThat(counts).containsEntry(TaskStatusCode.VOLLSTAENDIG, 1L);
    }

    @Test
    void statusPresentationClasses_areResolvedFromTypedStatusCodes() {
        TaskStatus submitted = new TaskStatus("ABGEGEBEN", "submitted", 3);
        TaskStatus needsRework = new TaskStatus("ÜBERARBEITUNG_NÖTIG", "rework", 4);

        assertThat(TaskStatusSupport.iconClass(submitted)).isEqualTo("fas fa-hourglass-half text-warning");
        assertThat(TaskStatusSupport.textColorClass(needsRework)).isEqualTo("text-danger");
        assertThat(TaskStatusSupport.badgeClass(needsRework)).isEqualTo("bg-danger");
        assertThat(TaskStatusSupport.iconClass(null)).isEqualTo("fas fa-circle text-secondary");
    }

    private UserTask userTask(String statusName) {
        UserTask userTask = new UserTask();
        userTask.setUser(new User());
        userTask.setTask(new Task());
        if (statusName != null) {
            userTask.setStatus(new TaskStatus(statusName, statusName, 0));
        }
        return userTask;
    }
}
