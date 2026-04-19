package com.example.studenttask.service;

import com.example.studenttask.dto.TaskContentLoadResultDto;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentTaskApiQueryService {

    private static final Logger log = LoggerFactory.getLogger(StudentTaskApiQueryService.class);

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskContentService taskContentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    public TaskContentLoadResultDto getTaskContent(Long taskId, String openIdSubject) {
        log.debug("Loading task content for task {} and user {}", taskId, openIdSubject);

        Optional<User> userOpt = userService.findByOpenIdSubject(openIdSubject);
        if (userOpt.isEmpty()) {
            log.warn("User not found while loading task content: {}", openIdSubject);
            return TaskContentLoadResultDto.unauthorized();
        }
        User user = userOpt.get();
        log.debug("Found user {} for content request", user.getId());

        Optional<Task> taskOpt = taskService.findById(taskId);
        if (taskOpt.isEmpty()) {
            log.warn("Task {} not found while loading content", taskId);
            return TaskContentLoadResultDto.notFound();
        }
        Task task = taskOpt.get();
        log.debug("Found task {} ({})", task.getId(), task.getTitle());

        Optional<UserTask> userTaskOpt = userTaskService.findByUserIdAndTaskId(user.getId(), task.getId());
        if (userTaskOpt.isEmpty()) {
            log.debug("No UserTask found for user {} and task {}", user.getId(), task.getId());
            return TaskContentLoadResultDto.success("");
        }
        UserTask userTask = userTaskOpt.get();
        log.debug("Found UserTask {}", userTask.getId());

        Optional<TaskContent> latestContent = taskContentService.getLatestContent(userTask);
        String content = "";
        if (latestContent.isPresent()) {
            content = latestContent.get().getContent();
            log.debug("Found latest content for UserTask {} with version {} and length {}",
                userTask.getId(),
                latestContent.get().getVersion(),
                content != null ? content.length() : null);
            log.debug("Content preview: {}", preview(content, 50));
        } else {
            log.debug("No content found for UserTask {}", userTask.getId());
        }

        return TaskContentLoadResultDto.success(content != null ? content : "");
    }

    private String preview(String content, int maxLength) {
        if (content == null) {
            return null;
        }
        return content.length() > maxLength ? content.substring(0, maxLength) + "..." : content;
    }
}
