package com.example.studenttask.service;

import com.example.studenttask.dto.TaskIframeViewDataDto;
import com.example.studenttask.exception.StudentResourceNotFoundException;
import com.example.studenttask.exception.UserAuthenticationRequiredException;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskIframeQueryService {

    @Autowired
    private StudentTaskViewSupportService studentTaskViewSupportService;

    @Autowired
    private UserService userService;

    public TaskIframeViewDataDto getTaskIframeViewData(
            Long taskId,
            String authenticationName,
            boolean teacherView,
            Integer version) {
        User targetUser = userService.findByOpenIdSubject(authenticationName)
            .orElseThrow(() -> new UserAuthenticationRequiredException("Benutzer nicht gefunden"));
        var task = studentTaskViewSupportService.findAssignedTask(targetUser, taskId)
            .orElseThrow(() -> new StudentResourceNotFoundException("Aufgabe nicht gefunden"));

        UserTask userTask = studentTaskViewSupportService.findOrCreateUserTask(targetUser, task);
        String currentContent = studentTaskViewSupportService.resolveCurrentContent(
            task,
            studentTaskViewSupportService.getRequestedContent(userTask, version),
            false
        );
        TaskView taskView = studentTaskViewSupportService.resolveTaskView(task);
        if (!studentTaskViewSupportService.hasRenderableTemplate(taskView)) {
            throw new StudentResourceNotFoundException("Aufgabenansicht nicht gefunden");
        }

        return new TaskIframeViewDataDto(
            task,
            taskView,
            userTask,
            currentContent,
            task.getDescription(),
            teacherView
        );
    }
}
