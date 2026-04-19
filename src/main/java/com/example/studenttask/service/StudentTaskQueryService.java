package com.example.studenttask.service;

import com.example.studenttask.dto.StudentTaskHistoryDataDto;
import com.example.studenttask.dto.StudentTaskVersionViewResultDto;
import com.example.studenttask.dto.StudentTaskViewDataDto;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.repository.UserTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StudentTaskQueryService {

    private static final Logger log = LoggerFactory.getLogger(StudentTaskQueryService.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskContentService taskContentService;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private TaskReviewService taskReviewService;

    public StudentTaskViewDataDto getTaskViewData(User student, Long taskId) {
        Task task = taskService.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Aufgabe nicht gefunden"));

        UserTask userTask = userTaskService.findByUserIdAndTaskId(student.getId(), task.getId())
            .orElseThrow(() -> new RuntimeException("Keine Berechtigung für diese Aufgabe"));

        Optional<TaskContent> latestContent = taskContentService.getLatestContent(userTask);
        TaskView taskView = task.getTaskView();
        if (taskView == null
                || taskView.getTemplatePath() == null
                || taskView.getTemplatePath().isBlank()) {
            throw new RuntimeException("TaskView nicht gefunden");
        }

        log.debug("Loading task content for userTask {} and task {}", userTask.getId(), task.getId());
        log.debug("Latest content present: {}", latestContent.isPresent());
        if (latestContent.isPresent()) {
            TaskContent content = latestContent.get();
            log.debug("Content id={}, version={}, length={}",
                content.getId(),
                content.getVersion(),
                content.getContent() != null ? content.getContent().length() : null);
            log.debug("Content preview: {}",
                content.getContent() != null && content.getContent().length() > 50
                    ? content.getContent().substring(0, 50) + "..."
                    : content.getContent());
        }
        log.debug("Default submission preview: {}",
            task.getDefaultSubmission() != null
                ? task.getDefaultSubmission().substring(0, Math.min(50, task.getDefaultSubmission().length()))
                : "null");

        String currentContent = resolveCurrentContent(task, latestContent.orElse(null));
        if (!currentContent.isEmpty()) {
            log.debug("Using current content: {}",
                currentContent.substring(0, Math.min(50, currentContent.length())) + "...");
        } else {
            log.debug("Using current content: ");
        }
        log.debug("Completed task content loading for userTask {}", userTask.getId());

        return new StudentTaskViewDataDto(task, userTask, taskView, currentContent, null, false);
    }

    public Optional<StudentTaskHistoryDataDto> getTaskHistoryData(User student, Long taskId) {
        Optional<Task> taskOpt = findAssignedTask(student, taskId);
        if (taskOpt.isEmpty()) {
            return Optional.empty();
        }

        Task task = taskOpt.get();
        UserTask userTask = findOrCreateHistoryUserTask(student, task);

        List<TaskContent> contentVersions = taskContentService.getAllContentVersions(userTask);
        List<TaskReview> reviews = taskReviewService.findByUserTaskOrderByReviewedAtDesc(userTask);

        return Optional.of(new StudentTaskHistoryDataDto(task, userTask, contentVersions, reviews));
    }

    public StudentTaskVersionViewResultDto getTaskVersionViewData(User student, Long taskId, Integer version) {
        Optional<Task> taskOpt = findAssignedTask(student, taskId);
        if (taskOpt.isEmpty()) {
            return StudentTaskVersionViewResultDto.redirect("redirect:/student/dashboard");
        }

        Task task = taskOpt.get();
        Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);
        if (userTaskOpt.isEmpty()) {
            return StudentTaskVersionViewResultDto.redirect("redirect:/student/tasks/" + taskId + "/history");
        }

        UserTask userTask = userTaskOpt.get();
        TaskView taskView = task.getTaskView();
        if (taskView == null) {
            return StudentTaskVersionViewResultDto.redirect("redirect:/student/tasks/" + taskId + "/history");
        }

        TaskContent versionContent = taskContentService.getContentByVersion(userTask, version);
        if (versionContent == null) {
            return StudentTaskVersionViewResultDto.redirect("redirect:/student/tasks/" + taskId + "/history");
        }

        return StudentTaskVersionViewResultDto.view(
            new StudentTaskViewDataDto(
                task,
                userTask,
                taskView,
                versionContent.getContent(),
                version,
                true
            )
        );
    }

    private Optional<Task> findAssignedTask(User student, Long taskId) {
        return taskService.findById(taskId)
            .filter(task -> task.getAssignedGroups().stream()
                .anyMatch(group -> student.getGroups().contains(group)));
    }

    private UserTask findOrCreateHistoryUserTask(User student, Task task) {
        Optional<UserTask> userTaskOpt = userTaskRepository.findByUserAndTask(student, task);
        if (userTaskOpt.isPresent()) {
            return userTaskOpt.get();
        }

        UserTask userTask = new UserTask();
        userTask.setUser(student);
        userTask.setTask(task);
        userTask.setStartedAt(LocalDateTime.now());
        userTask.setStatus(taskStatusService.getDefaultStatus());
        return userTaskRepository.save(userTask);
    }

    private String resolveCurrentContent(Task task, TaskContent content) {
        if (content != null
                && content.getContent() != null
                && !content.getContent().trim().isEmpty()) {
            return content.getContent();
        }

        return task.getDefaultSubmission() != null ? task.getDefaultSubmission() : "";
    }
}
