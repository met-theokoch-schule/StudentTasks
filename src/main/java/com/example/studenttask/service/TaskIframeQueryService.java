package com.example.studenttask.service;

import com.example.studenttask.dto.TaskIframeViewDataDto;
import com.example.studenttask.dto.TaskIframeViewResultDto;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskIframeQueryService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskContentService taskContentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskViewService taskViewService;

    public TaskIframeViewResultDto getTaskIframeViewData(
            Long taskId,
            String authenticationName,
            boolean teacherView,
            Integer version) {
        Optional<Task> taskOpt = taskService.findById(taskId);
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

        UserTask userTask = userTaskService.findOrCreateUserTask(targetUserOpt.get(), task);
        String currentContent = resolveCurrentContent(task, userTask, version);
        TaskView taskView = resolveTaskView(task);
        if (taskView == null || taskView.getTemplatePath() == null || taskView.getTemplatePath().isBlank()) {
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

    private String resolveCurrentContent(Task task, UserTask userTask, Integer version) {
        TaskContent content = null;
        if (version != null) {
            content = taskContentService.getContentByVersion(userTask, version);
        } else {
            content = taskContentService.getLatestContent(userTask).orElse(null);
        }

        if (content != null && content.getContent() != null) {
            return content.getContent();
        }

        return task.getDefaultSubmission() != null ? task.getDefaultSubmission() : "";
    }

    private TaskView resolveTaskView(Task task) {
        TaskView taskView = task.getTaskView();
        if (taskView == null) {
            return null;
        }

        Long taskViewId = taskView.getId();
        if (taskViewId == null) {
            return taskView;
        }

        return taskViewService.findById(taskViewId).orElse(taskView);
    }
}
