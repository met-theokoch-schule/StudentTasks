package com.example.studenttask.service;

import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.UserTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentTaskApiCommandService {

    private static final Logger log = LoggerFactory.getLogger(StudentTaskApiCommandService.class);

    @Autowired
    private StudentTaskApiAccessService studentTaskApiAccessService;

    @Autowired
    private TaskContentService taskContentService;

    public TaskContent saveTeacherTaskContent(Long userTaskId, String content, String authenticationName) {
        log.debug("Saving teacher content for userTask {} by user {} with length {}",
            userTaskId,
            authenticationName,
            content != null ? content.length() : null);

        UserTask userTask = studentTaskApiAccessService.requireUserTask(userTaskId);
        log.debug("Found UserTask {} (user={}, task={})",
            userTask.getId(), userTask.getUser().getId(), userTask.getTask().getId());

        TaskContent savedContent = taskContentService.saveContent(userTask, content, false);
        log.debug("Saved content {} with version {} for UserTask {}",
            savedContent.getId(), savedContent.getVersion(), userTask.getId());

        return savedContent;
    }

    public TaskContent saveTaskContent(Long taskId, String openIdSubject, String content) {
        log.debug("Saving task content for task {} and user {}", taskId, openIdSubject);
        log.debug("Content length: {}", content != null ? content.length() : null);
        if (content != null && !content.isEmpty()) {
            log.debug("Content preview: {}", preview(content, 100));
        }

        UserTask userTask = studentTaskApiAccessService.findOrCreateUserTask(taskId, openIdSubject);
        log.debug("Using UserTask {} (user={}, task={})",
            userTask.getId(), userTask.getUser().getId(), userTask.getTask().getId());

        TaskContent savedContent = taskContentService.saveContent(userTask, content, false);
        log.debug("Saved content {} version {} for task {}",
            savedContent.getId(), savedContent.getVersion(), taskId);
        log.debug("Saved content length: {}",
            savedContent.getContent() != null ? savedContent.getContent().length() : null);
        log.debug("Saved at: {}", savedContent.getSavedAt());
        log.debug("Submitted flag: {}", savedContent.isSubmitted());

        return savedContent;
    }

    public void submitTask(Long taskId, String openIdSubject, String content) {
        log.debug("Submitting task {} for user {}", taskId, openIdSubject);

        UserTask userTask = studentTaskApiAccessService.findOrCreateUserTask(taskId, openIdSubject);

        if (content != null) {
            log.debug("Submitting provided content for UserTask {} with length {}",
                userTask.getId(), content.length());
            taskContentService.submitContent(userTask, content);
            return;
        }

        Optional<TaskContent> latestContentOpt = taskContentService.getLatestContent(userTask);
        if (latestContentOpt.isPresent()) {
            log.debug("Submitting latest saved content for UserTask {}", userTask.getId());
            taskContentService.submitContent(userTask, latestContentOpt.get().getContent());
            return;
        }

        log.debug("No content available to submit for UserTask {}", userTask.getId());
    }

    private String preview(String content, int maxLength) {
        if (content == null) {
            return null;
        }
        return content.length() > maxLength ? content.substring(0, maxLength) + "..." : content;
    }
}
