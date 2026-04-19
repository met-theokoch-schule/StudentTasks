package com.example.studenttask.service;

import com.example.studenttask.dto.TaskIframeViewDataDto;
import com.example.studenttask.dto.TaskIframeViewResultDto;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskIframeQueryService {

    @Autowired
    private StudentTaskViewSupportService studentTaskViewSupportService;

    @Autowired
    private UserService userService;

    public TaskIframeViewResultDto getTaskIframeViewData(
            Long taskId,
            String authenticationName,
            boolean teacherView,
            Integer version) {
        Optional<Task> taskOpt = studentTaskViewSupportService.findTask(taskId);
        if (taskOpt.isEmpty()) {
            return TaskIframeViewResultDto.redirect("redirect:/student/dashboard");
        }

        Task task = taskOpt.get();
        Optional<User> targetUserOpt = userService.findByOpenIdSubject(authenticationName);
        if (targetUserOpt.isEmpty()) {
            return TaskIframeViewResultDto.redirect(
                teacherView ? "redirect:/teacher/dashboard" : "redirect:/login"
            );
        }

        UserTask userTask = studentTaskViewSupportService.findOrCreateUserTask(targetUserOpt.get(), task);
        String currentContent = studentTaskViewSupportService.resolveCurrentContent(
            task,
            studentTaskViewSupportService.getRequestedContent(userTask, version),
            false
        );
        TaskView taskView = studentTaskViewSupportService.resolveTaskView(task);
        if (!studentTaskViewSupportService.hasRenderableTemplate(taskView)) {
            return TaskIframeViewResultDto.redirect(
                teacherView ? "redirect:/teacher/dashboard" : "redirect:/student/dashboard"
            );
        }

        return TaskIframeViewResultDto.view(new TaskIframeViewDataDto(
            task,
            taskView,
            userTask,
            currentContent,
            task.getDescription(),
            teacherView
        ));
    }
}
