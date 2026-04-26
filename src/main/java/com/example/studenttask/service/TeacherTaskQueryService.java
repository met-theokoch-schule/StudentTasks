package com.example.studenttask.service;

import com.example.studenttask.dto.TeacherSubmissionContentViewDto;
import com.example.studenttask.dto.TeacherTaskFormDataDto;
import com.example.studenttask.dto.TeacherTaskFormDto;
import com.example.studenttask.dto.TeacherSubmissionReviewDataDto;
import com.example.studenttask.dto.TeacherTaskListDataDto;
import com.example.studenttask.dto.TeacherTaskSubmissionsDataDto;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeacherTaskQueryService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskReviewService taskReviewService;

    @Autowired
    private TaskContentService taskContentService;

    @Autowired
    private StudentTaskViewSupportService studentTaskViewSupportService;

    @Autowired
    private TaskViewService taskViewService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UnitTitleService unitTitleService;

    public TeacherTaskListDataDto getTaskListData(User teacher, String filter) {
        List<Task> tasks = "all".equals(filter)
            ? taskService.findAllOrderByCreatedAtDesc()
            : taskService.findByCreatedByOrderByCreatedAtDesc(teacher);

        return new TeacherTaskListDataDto(tasks, groupTasksByUnitTitle(tasks));
    }

    public Optional<TeacherTaskSubmissionsDataDto> getTaskSubmissionsData(Long taskId, User teacher) {
        return taskService.findById(taskId)
            .map(task -> new TeacherTaskSubmissionsDataDto(
                task,
                userTaskService.findByTask(task),
                teacher != null && task.getCreatedBy().equals(teacher)
            ));
    }

    public Optional<TeacherSubmissionReviewDataDto> getSubmissionReviewData(Long userTaskId) {
        return userTaskService.findById(userTaskId)
            .map(userTask -> new TeacherSubmissionReviewDataDto(
                userTask,
                taskReviewService.findByUserTaskOrderByReviewedAtDesc(userTask),
                taskReviewService.getTeacherReviewStatuses(),
                taskContentService.getVersionsWithSubmissionStatus(userTask)
            ));
    }

    public Optional<TeacherSubmissionContentViewDto> getSubmissionContentViewData(Long userTaskId, Integer version) {
        return userTaskService.findById(userTaskId)
            .map(userTask -> {
                Task task = userTask.getTask();
                TaskContent content = studentTaskViewSupportService.getRequestedContent(userTask, version, true);

                String currentContent = content != null
                    ? content.getContent()
                    : task.getDefaultSubmission() != null ? task.getDefaultSubmission() : "";
                Integer resolvedVersion = content != null ? content.getVersion() : 1;

                return new TeacherSubmissionContentViewDto(
                    task,
                    userTask,
                    currentContent,
                    resolvedVersion,
                    studentTaskViewSupportService.resolveTemplatePath(task, "taskviews/simple-text.html")
                );
            });
    }

    public TeacherTaskFormDataDto getCreateTaskFormData() {
        return buildTaskFormData(null);
    }

    public Optional<TeacherTaskFormDataDto> getEditTaskFormData(Long taskId) {
        return taskService.findById(taskId)
            .map(this::buildTaskFormData);
    }

    public boolean hasTaskView(Long taskViewId) {
        return taskViewId != null && taskViewService.findById(taskViewId).isPresent();
    }

    public boolean hasUnitTitle(String unitTitleId) {
        return unitTitleId != null
            && !unitTitleId.trim().isEmpty()
            && unitTitleService.findById(unitTitleId) != null;
    }

    public boolean hasAllGroups(List<Long> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) {
            return true;
        }

        Set<Long> distinctGroupIds = new HashSet<>(groupIds);
        if (distinctGroupIds.contains(null)) {
            return false;
        }

        return groupService.findAllById(groupIds).size() == distinctGroupIds.size();
    }

    private Map<UnitTitle, List<Task>> groupTasksByUnitTitle(List<Task> tasks) {
        Map<UnitTitle, List<Task>> tasksByUnitTitle = new LinkedHashMap<>();
        UnitTitle noUnitTitle = null;

        for (Task task : tasks) {
            UnitTitle key = task.getUnitTitle();
            if (key == null) {
                if (noUnitTitle == null) {
                    noUnitTitle = new UnitTitle();
                    noUnitTitle.setName("Aufgaben ohne Thema");
                    noUnitTitle.setDescription("Aufgaben die keinem Thema zugeordnet sind");
                }
                key = noUnitTitle;
            }
            tasksByUnitTitle.computeIfAbsent(key, ignored -> new ArrayList<>()).add(task);
        }

        return tasksByUnitTitle;
    }

    private TeacherTaskFormDataDto buildTaskFormData(Task task) {
        return new TeacherTaskFormDataDto(
            task,
            toTaskForm(task),
            taskViewService.findAllActive(),
            groupService.findAll(),
            unitTitleService.findAllActive()
        );
    }

    private TeacherTaskFormDto toTaskForm(Task task) {
        TeacherTaskFormDto taskForm = new TeacherTaskFormDto();
        if (task == null) {
            return taskForm;
        }

        taskForm.setTitle(task.getTitle());
        taskForm.setDescription(task.getDescription());
        taskForm.setTutorial(task.getTutorial());
        taskForm.setDefaultSubmission(task.getDefaultSubmission());
        taskForm.setDueDate(task.getDueDate());
        taskForm.setIsActive(task.getIsActive());
        taskForm.setTaskViewId(task.getTaskView() != null ? task.getTaskView().getId() : null);
        taskForm.setUnitTitleId(task.getUnitTitle() != null ? task.getUnitTitle().getId() : null);
        taskForm.setSelectedGroups(task.getAssignedGroups().stream()
            .map(Group::getId)
            .collect(Collectors.toList()));

        return taskForm;
    }
}
