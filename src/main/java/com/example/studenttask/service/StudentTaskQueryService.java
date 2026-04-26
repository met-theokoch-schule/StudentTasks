package com.example.studenttask.service;

import com.example.studenttask.dto.StudentTaskHistoryDataDto;
import com.example.studenttask.dto.StudentTaskViewDataDto;
import com.example.studenttask.exception.StudentAccessDeniedException;
import com.example.studenttask.exception.StudentResourceNotFoundException;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentTaskQueryService {

    private static final Logger log = LoggerFactory.getLogger(StudentTaskQueryService.class);

    @Autowired
    private TaskContentService taskContentService;

    @Autowired
    private TaskReviewService taskReviewService;

    @Autowired
    private StudentTaskViewSupportService studentTaskViewSupportService;

    public StudentTaskViewDataDto getTaskViewData(User student, Long taskId) {
        Task task = studentTaskViewSupportService.findTask(taskId)
            .orElseThrow(() -> new StudentResourceNotFoundException("Aufgabe nicht gefunden"));

        UserTask userTask = studentTaskViewSupportService.findExistingUserTask(student, task)
            .orElseThrow(() -> new StudentAccessDeniedException("Keine Berechtigung für diese Aufgabe"));

        TaskContent latestContent = studentTaskViewSupportService.getRequestedContent(userTask, null);
        TaskView taskView = studentTaskViewSupportService.resolveTaskView(task);
        if (!studentTaskViewSupportService.hasRenderableTemplate(taskView)) {
            throw new StudentResourceNotFoundException("Aufgabenansicht nicht gefunden");
        }

        log.debug("Loading task content for userTask {} and task {}", userTask.getId(), task.getId());
        log.debug("Latest content present: {}", latestContent != null);
        if (latestContent != null) {
            log.debug("Content id={}, version={}, length={}",
                latestContent.getId(),
                latestContent.getVersion(),
                latestContent.getContent() != null ? latestContent.getContent().length() : null);
            log.debug("Content preview: {}",
                latestContent.getContent() != null && latestContent.getContent().length() > 50
                    ? latestContent.getContent().substring(0, 50) + "..."
                    : latestContent.getContent());
        }
        log.debug("Default submission preview: {}",
            task.getDefaultSubmission() != null
                ? task.getDefaultSubmission().substring(0, Math.min(50, task.getDefaultSubmission().length()))
                : "null");

        String currentContent = studentTaskViewSupportService.resolveCurrentContent(task, latestContent, true);
        if (!currentContent.isEmpty()) {
            log.debug("Using current content: {}",
                currentContent.substring(0, Math.min(50, currentContent.length())) + "...");
        } else {
            log.debug("Using current content: ");
        }
        log.debug("Completed task content loading for userTask {}", userTask.getId());

        return new StudentTaskViewDataDto(task, userTask, taskView, currentContent, null, false);
    }

    public StudentTaskHistoryDataDto getTaskHistoryData(User student, Long taskId) {
        Task task = studentTaskViewSupportService.findAssignedTask(student, taskId)
            .orElseThrow(() -> new StudentResourceNotFoundException("Aufgabe nicht gefunden"));
        UserTask userTask = studentTaskViewSupportService.findOrCreateUserTask(student, task);

        List<TaskContent> contentVersions = taskContentService.getAllContentVersions(userTask);
        List<TaskReview> reviews = taskReviewService.findByUserTaskOrderByReviewedAtDesc(userTask);

        return new StudentTaskHistoryDataDto(task, userTask, contentVersions, reviews);
    }

    public StudentTaskViewDataDto getTaskVersionViewData(User student, Long taskId, Integer version) {
        Task task = studentTaskViewSupportService.findAssignedTask(student, taskId)
            .orElseThrow(() -> new StudentResourceNotFoundException("Aufgabe nicht gefunden"));
        Optional<UserTask> userTaskOpt = studentTaskViewSupportService.findExistingUserTask(student, task);
        if (userTaskOpt.isEmpty()) {
            throw new StudentResourceNotFoundException("Version nicht gefunden");
        }

        UserTask userTask = userTaskOpt.get();
        TaskView taskView = studentTaskViewSupportService.resolveTaskView(task);
        if (!studentTaskViewSupportService.hasRenderableTemplate(taskView)) {
            throw new StudentResourceNotFoundException("Aufgabenansicht nicht gefunden");
        }

        TaskContent versionContent = studentTaskViewSupportService.getRequestedContent(userTask, version);
        if (versionContent == null) {
            throw new StudentResourceNotFoundException("Version nicht gefunden");
        }

        return new StudentTaskViewDataDto(
            task,
            userTask,
            taskView,
            versionContent.getContent(),
            version,
            true
        );
    }
}
