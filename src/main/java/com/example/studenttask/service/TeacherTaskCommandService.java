package com.example.studenttask.service;

import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TeacherTaskCommandService {

    private static final Logger log = LoggerFactory.getLogger(TeacherTaskCommandService.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private TaskViewService taskViewService;

    @Autowired
    private UnitTitleService unitTitleService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskReviewService taskReviewService;

    public Task createTask(Task task, User teacher, String taskViewId, String unitTitleId, List<Long> selectedGroupIds) {
        task.setCreatedBy(teacher);
        task.setAssignedGroups(resolveAssignedGroups(selectedGroupIds));
        resolveTaskView(taskViewId).ifPresent(task::setTaskView);
        task.setUnitTitle(resolveUnitTitle(unitTitleId));
        return taskService.save(task);
    }

    public Task updateTask(Long id, Task taskUpdates, String taskViewId, String unitTitleId,
            List<Long> selectedGroupIds, String tutorial) {
        Task existingTask = taskService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));

        existingTask.setTitle(taskUpdates.getTitle());
        existingTask.setDescription(taskUpdates.getDescription());
        existingTask.setDefaultSubmission(taskUpdates.getDefaultSubmission());
        existingTask.setTutorial(tutorial);
        existingTask.setIsActive(taskUpdates.getIsActive());
        existingTask.setAssignedGroups(resolveAssignedGroups(selectedGroupIds));
        resolveTaskView(taskViewId).ifPresent(existingTask::setTaskView);
        existingTask.setUnitTitle(resolveUnitTitle(unitTitleId));

        return taskService.save(existingTask);
    }

    public void deleteTask(Long id) {
        Task task = taskService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));
        taskService.delete(task);
    }

    public void deleteTask(Long id, User teacher) {
        Task task = taskService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));

        if (teacher == null || task.getCreatedBy() == null || !task.getCreatedBy().equals(teacher)) {
            throw new IllegalStateException("Zugriff verweigert");
        }

        taskService.delete(task);
    }

    public boolean submitReview(Long userTaskId, String reviewerSubject, Long statusId, String comment,
            String currentVersionStr) {
        Optional<UserTask> userTaskOpt = userTaskService.findById(userTaskId);
        if (userTaskOpt.isEmpty()) {
            return false;
        }

        Optional<User> reviewerOpt = userService.findByOpenIdSubject(reviewerSubject);
        if (reviewerOpt.isEmpty()) {
            return false;
        }

        UserTask userTask = userTaskOpt.get();
        User reviewer = reviewerOpt.get();
        Integer currentVersion = parseOptionalInteger(currentVersionStr);

        taskReviewService.createReview(userTask, reviewer, statusId, comment, currentVersion);
        userTaskService.save(userTask);
        return true;
    }

    private Set<Group> resolveAssignedGroups(List<Long> selectedGroupIds) {
        if (selectedGroupIds == null || selectedGroupIds.isEmpty()) {
            return new HashSet<>();
        }

        return new HashSet<>(groupService.findAllById(selectedGroupIds));
    }

    private Optional<TaskView> resolveTaskView(String taskViewId) {
        if (taskViewId == null || taskViewId.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            return taskViewService.findById(Long.parseLong(taskViewId));
        } catch (NumberFormatException e) {
            log.warn("Invalid taskViewId format: {}", taskViewId);
            return Optional.empty();
        }
    }

    private UnitTitle resolveUnitTitle(String unitTitleId) {
        if (unitTitleId == null || unitTitleId.trim().isEmpty()) {
            return null;
        }

        return unitTitleService.findById(unitTitleId);
    }

    private Integer parseOptionalInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return Integer.parseInt(value);
    }
}
