package com.example.studenttask.service;

import com.example.studenttask.dto.TeacherSubmissionContentViewDto;
import com.example.studenttask.dto.TeacherTaskFormDataDto;
import com.example.studenttask.dto.TeacherTaskFormDto;
import com.example.studenttask.dto.TeacherTaskListItemDto;
import com.example.studenttask.dto.TeacherSubmissionReviewDataDto;
import com.example.studenttask.dto.TeacherTaskListDataDto;
import com.example.studenttask.dto.TeacherTaskSubmissionsDataDto;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskStatusCode;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

    @Autowired
    private UserService userService;

    @Value("${app.teacher.views.include-teachers:false}")
    private boolean includeTeachersInTeacherViews;

    public TeacherTaskListDataDto getTaskListData(User teacher, String filter) {
        List<Task> tasks = "all".equals(filter)
            ? taskService.findAllOrderByCreatedAtDesc()
            : taskService.findByCreatedByOrderByCreatedAtDesc(teacher);
        List<TeacherTaskListItemDto> taskItems = tasks.stream()
            .map(this::toTaskListItem)
            .toList();

        return new TeacherTaskListDataDto(taskItems, groupTasksByUnitTitle(taskItems));
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
        return getSubmissionReviewData(userTaskId, null);
    }

    public Optional<TeacherSubmissionReviewDataDto> getSubmissionReviewData(Long userTaskId, User teacher) {
        return userTaskService.findById(userTaskId)
            .map(userTask -> new TeacherSubmissionReviewDataDto(
                userTask,
                taskReviewService.findByUserTaskOrderByReviewedAtDesc(userTask),
                taskReviewService.getTeacherReviewStatuses(),
                taskContentService.getVersionsWithSubmissionStatus(userTask),
                findNextReviewForTask(userTask, teacher).orElse(null)
            ));
    }

    public Optional<UserTask> findNextReviewForTask(Long currentUserTaskId, User teacher) {
        return userTaskService.findById(currentUserTaskId)
            .flatMap(userTask -> findNextReviewForTask(userTask, teacher));
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

    private Map<UnitTitle, List<TeacherTaskListItemDto>> groupTasksByUnitTitle(List<TeacherTaskListItemDto> tasks) {
        Map<UnitTitle, List<TeacherTaskListItemDto>> tasksByUnitTitle = new LinkedHashMap<>();
        UnitTitle noUnitTitle = null;

        for (TeacherTaskListItemDto taskItem : tasks) {
            Task task = taskItem.getTask();
            UnitTitle key = task.getUnitTitle();
            if (key == null) {
                if (noUnitTitle == null) {
                    noUnitTitle = new UnitTitle();
                    noUnitTitle.setName("Aufgaben ohne Thema");
                    noUnitTitle.setDescription("Aufgaben die keinem Thema zugeordnet sind");
                }
                key = noUnitTitle;
            }
            tasksByUnitTitle.computeIfAbsent(key, ignored -> new ArrayList<>()).add(taskItem);
        }

        return tasksByUnitTitle;
    }

    private TeacherTaskListItemDto toTaskListItem(Task task) {
        List<UserTask> userTasks = userTaskService.findByTask(task);
        boolean hasSubmissions = !userTasks.isEmpty();
        boolean hasPendingReviews = userTasks.stream()
            .anyMatch(userTask -> TaskStatusSupport.hasCode(userTask.getStatus(), TaskStatusCode.ABGEGEBEN));

        return new TeacherTaskListItemDto(task, hasSubmissions, hasPendingReviews);
    }

    private Optional<UserTask> findNextReviewForTask(UserTask currentUserTask, User teacher) {
        if (currentUserTask == null || currentUserTask.getTask() == null || teacher == null) {
            return Optional.empty();
        }

        Task task = currentUserTask.getTask();
        List<UserTask> reviewCandidates = userTaskService.findByTask(task).stream()
            .filter(userTask -> !isSameUserTask(userTask, currentUserTask))
            .filter(userTask -> isEligibleNextReview(userTask, teacher, task))
            .sorted(userTaskReviewOrder())
            .toList();

        if (reviewCandidates.isEmpty()) {
            return Optional.empty();
        }

        Long currentUserTaskId = currentUserTask.getId();
        if (currentUserTaskId != null) {
            Optional<UserTask> nextAfterCurrent = reviewCandidates.stream()
                .filter(userTask -> userTask.getId() != null && userTask.getId() > currentUserTaskId)
                .findFirst();
            if (nextAfterCurrent.isPresent()) {
                return nextAfterCurrent;
            }
        }

        return Optional.of(reviewCandidates.get(0));
    }

    private boolean isEligibleNextReview(UserTask userTask, User teacher, Task task) {
        if (!TaskStatusSupport.hasCode(userTask.getStatus(), TaskStatusCode.ABGEGEBEN)) {
            return false;
        }

        if (!shouldIncludePendingReviewUser(userTask.getUser())) {
            return false;
        }

        return sharesAssignedGroup(safeGroups(teacher), safeGroups(userTask.getUser()), safeGroups(task.getAssignedGroups()));
    }

    private boolean sharesAssignedGroup(Set<Group> teacherGroups, Set<Group> studentGroups, Set<Group> assignedGroups) {
        for (Group assignedGroup : assignedGroups) {
            if (teacherGroups.contains(assignedGroup) && studentGroups.contains(assignedGroup)) {
                return true;
            }
        }
        return false;
    }

    private Set<Group> safeGroups(User user) {
        return user == null ? Collections.emptySet() : safeGroups(user.getGroups());
    }

    private Set<Group> safeGroups(Set<Group> groups) {
        return groups == null ? Collections.emptySet() : new LinkedHashSet<>(groups);
    }

    private boolean shouldIncludePendingReviewUser(User user) {
        if (user == null) {
            return false;
        }
        return userService.hasStudentRole(user)
            || (includeTeachersInTeacherViews && userService.hasTeacherRole(user));
    }

    private Comparator<UserTask> userTaskReviewOrder() {
        return Comparator
            .comparing(UserTask::getId, Comparator.nullsLast(Comparator.naturalOrder()));
    }

    private boolean isSameUserTask(UserTask first, UserTask second) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null || first.getId() == null || second.getId() == null) {
            return false;
        }
        return first.getId().equals(second.getId());
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
        taskForm.setHoursDescription(task.getHoursDescription());
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
