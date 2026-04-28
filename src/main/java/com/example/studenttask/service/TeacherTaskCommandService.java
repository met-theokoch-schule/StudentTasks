package com.example.studenttask.service;

import com.example.studenttask.dto.TeacherTaskFormDto;
import com.example.studenttask.exception.TeacherAccessDeniedException;
import com.example.studenttask.exception.TeacherAuthenticationRequiredException;
import com.example.studenttask.exception.TeacherResourceNotFoundException;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class TeacherTaskCommandService {

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

    public Task createTask(User teacher, TeacherTaskFormDto taskForm) {
        Task task = new Task();
        task.setCreatedBy(teacher);
        applyTaskForm(task, taskForm, false);
        return taskService.save(task);
    }

    public Task updateTask(Long id, TeacherTaskFormDto taskForm) {
        Task existingTask = taskService.findById(id)
            .orElseThrow(() -> new TeacherResourceNotFoundException("Aufgabe nicht gefunden"));

        applyTaskForm(existingTask, taskForm, true);
        return taskService.save(existingTask);
    }

    public void deleteTask(Long id, User teacher) {
        Task task = taskService.findById(id)
            .orElseThrow(() -> new TeacherResourceNotFoundException("Aufgabe nicht gefunden"));

        if (teacher == null || task.getCreatedBy() == null || !task.getCreatedBy().equals(teacher)) {
            throw new TeacherAccessDeniedException("Zugriff auf diese Aufgabe verweigert");
        }

        taskService.delete(task);
    }

    public void submitReview(Long userTaskId, String reviewerSubject, Long statusId, String comment,
            String currentVersionStr) {
        UserTask userTask = userTaskService.findById(userTaskId)
            .orElseThrow(() -> new TeacherResourceNotFoundException("Abgabe nicht gefunden"));
        User reviewer = userService.findByOpenIdSubject(reviewerSubject)
            .orElseThrow(() -> new TeacherAuthenticationRequiredException("Benutzer nicht gefunden"));
        Integer currentVersion = parseOptionalInteger(currentVersionStr);

        taskReviewService.createReview(userTask, reviewer, statusId, comment, currentVersion);
        userTaskService.save(userTask);
    }

    private void applyTaskForm(Task task, TeacherTaskFormDto taskForm, boolean preserveExistingTaskViewOnMissingValue) {
        task.setTitle(taskForm.getTitle());
        task.setDescription(taskForm.getDescription());
        task.setHoursDescription(taskForm.getHoursDescription());
        task.setDefaultSubmission(taskForm.getDefaultSubmission());
        task.setTutorial(taskForm.getTutorial());
        task.setDueDate(taskForm.getDueDate());
        task.setIsActive(Boolean.TRUE.equals(taskForm.getIsActive()));
        task.setAssignedGroups(resolveAssignedGroups(taskForm.getSelectedGroups()));

        if (taskForm.getTaskViewId() != null) {
            task.setTaskView(requireTaskView(taskForm.getTaskViewId()));
        } else if (!preserveExistingTaskViewOnMissingValue) {
            task.setTaskView(null);
        }

        task.setUnitTitle(requireUnitTitle(taskForm.getUnitTitleId()));
    }

    private Set<Group> resolveAssignedGroups(List<Long> selectedGroupIds) {
        if (selectedGroupIds == null || selectedGroupIds.isEmpty()) {
            return new HashSet<>();
        }

        long distinctGroupCount = selectedGroupIds.stream()
            .filter(Objects::nonNull)
            .distinct()
            .count();
        if (distinctGroupCount != selectedGroupIds.size()) {
            throw new TeacherResourceNotFoundException("Eine oder mehrere Gruppen wurden nicht gefunden");
        }

        List<Group> resolvedGroups = groupService.findAllById(selectedGroupIds);
        if (resolvedGroups.size() != distinctGroupCount) {
            throw new TeacherResourceNotFoundException("Eine oder mehrere Gruppen wurden nicht gefunden");
        }

        return new HashSet<>(resolvedGroups);
    }

    private TaskView requireTaskView(Long taskViewId) {
        return taskViewService.findById(taskViewId)
            .orElseThrow(() -> new TeacherResourceNotFoundException("Aufgabentyp nicht gefunden"));
    }

    private UnitTitle requireUnitTitle(String unitTitleId) {
        if (unitTitleId == null || unitTitleId.trim().isEmpty()) {
            return null;
        }

        UnitTitle unitTitle = unitTitleService.findById(unitTitleId);
        if (unitTitle == null) {
            throw new TeacherResourceNotFoundException("Thema nicht gefunden");
        }
        return unitTitle;
    }

    private Integer parseOptionalInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return Integer.parseInt(value);
    }
}
