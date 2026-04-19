package com.example.studenttask.service;

import com.example.studenttask.dto.TeacherSubmissionContentViewDto;
import com.example.studenttask.dto.TeacherTaskFormDataDto;
import com.example.studenttask.dto.TeacherSubmissionReviewDataDto;
import com.example.studenttask.dto.TeacherTaskListDataDto;
import com.example.studenttask.dto.TeacherTaskSubmissionsDataDto;
import com.example.studenttask.dto.VersionWithSubmissionStatus;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherTaskQueryServiceTest {

    @Mock
    private TaskService taskService;

    @Mock
    private UserTaskService userTaskService;

    @Mock
    private TaskReviewService taskReviewService;

    @Mock
    private TaskContentService taskContentService;

    @Mock
    private TaskViewService taskViewService;

    @Mock
    private GroupService groupService;

    @Mock
    private UnitTitleService unitTitleService;

    @InjectMocks
    private TeacherTaskQueryService teacherTaskQueryService;

    @Test
    void getTaskListData_groupsTasksByUnitTitleAndFallbackTopic() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        UnitTitle basics = new UnitTitle("basic", "Basics", "desc", 10);
        Task taskWithUnit = task(20L, "Worksheet", teacher, group, basics);
        Task taskWithoutUnit = task(21L, "Freeform", teacher, group, null);

        when(taskService.findByCreatedByOrderByCreatedAtDesc(teacher)).thenReturn(List.of(taskWithUnit, taskWithoutUnit));

        TeacherTaskListDataDto taskListData = teacherTaskQueryService.getTaskListData(teacher, "own");

        assertThat(taskListData.getTasks()).containsExactly(taskWithUnit, taskWithoutUnit);
        assertThat(taskListData.getTasksByUnitTitle()).hasSize(2);
        assertThat(taskListData.getTasksByUnitTitle().get(basics)).containsExactly(taskWithUnit);
        assertThat(taskListData.getTasksByUnitTitle().keySet())
            .anySatisfy(unitTitle -> assertThat(unitTitle.getName()).isEqualTo("Aufgaben ohne Thema"));
    }

    @Test
    void getTaskSubmissionsData_returnsTaskUserTasksAndOwnershipFlag() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        Task task = task(20L, "Worksheet", teacher, group, null);
        UserTask userTask = userTask(student(2L, "Student", group), task, status("ABGEGEBEN"));

        when(taskService.findById(20L)).thenReturn(Optional.of(task));
        when(userTaskService.findByTask(task)).thenReturn(List.of(userTask));

        Optional<TeacherTaskSubmissionsDataDto> submissionsDataOpt =
            teacherTaskQueryService.getTaskSubmissionsData(20L, teacher);

        assertThat(submissionsDataOpt).isPresent();
        TeacherTaskSubmissionsDataDto submissionsData = submissionsDataOpt.get();
        assertThat(submissionsData.getTask()).isSameAs(task);
        assertThat(submissionsData.getUserTasks()).containsExactly(userTask);
        assertThat(submissionsData.isOwnTask()).isTrue();
    }

    @Test
    void getSubmissionReviewData_loadsReviewsStatusesAndVersionData() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        UserTask userTask = userTask(student(2L, "Student", group), task(20L, "Worksheet", teacher, group, null), status("ABGEGEBEN"));
        TaskReview review = new TaskReview();
        TaskStatus complete = status("VOLLSTAENDIG");
        VersionWithSubmissionStatus versionStatus = new VersionWithSubmissionStatus(2, true, "v2 19.04.26 09:00 👁");

        when(userTaskService.findById(30L)).thenReturn(Optional.of(userTask));
        when(taskReviewService.findByUserTaskOrderByReviewedAtDesc(userTask)).thenReturn(List.of(review));
        when(taskReviewService.getTeacherReviewStatuses()).thenReturn(List.of(complete));
        when(taskContentService.getVersionsWithSubmissionStatus(30L)).thenReturn(List.of(versionStatus));

        Optional<TeacherSubmissionReviewDataDto> reviewDataOpt =
            teacherTaskQueryService.getSubmissionReviewData(30L);

        assertThat(reviewDataOpt).isPresent();
        TeacherSubmissionReviewDataDto reviewData = reviewDataOpt.get();
        assertThat(reviewData.getUserTask()).isSameAs(userTask);
        assertThat(reviewData.getReviews()).containsExactly(review);
        assertThat(reviewData.getStatuses()).containsExactly(complete);
        assertThat(reviewData.getVersionsWithStatus()).containsExactly(versionStatus);
    }

    @Test
    void getSubmissionContentViewData_usesSpecificVersionAndFallbackTemplate() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        Task task = task(20L, "Worksheet", teacher, group, null);
        task.setDefaultSubmission("Default");
        UserTask userTask = userTask(student(2L, "Student", group), task, status("ABGEGEBEN"));

        TaskContent content = new TaskContent();
        content.setContent("Specific Content");
        content.setVersion(4);

        when(userTaskService.findById(30L)).thenReturn(Optional.of(userTask));
        when(taskContentService.getContentByVersion(userTask, 4)).thenReturn(content);

        Optional<TeacherSubmissionContentViewDto> contentViewOpt =
            teacherTaskQueryService.getSubmissionContentViewData(30L, 4);

        assertThat(contentViewOpt).isPresent();
        TeacherSubmissionContentViewDto contentView = contentViewOpt.get();
        assertThat(contentView.getTask()).isSameAs(task);
        assertThat(contentView.getCurrentContent()).isEqualTo("Specific Content");
        assertThat(contentView.getVersion()).isEqualTo(4);
        assertThat(contentView.getTemplatePath()).isEqualTo("taskviews/simple-text.html");
    }

    @Test
    void getSubmissionContentViewData_fallsBackToLatestContentAndTaskViewTemplate() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        Task task = task(20L, "Worksheet", teacher, group, null);
        TaskView taskView = new TaskView();
        taskView.setTemplatePath("taskviews/custom-template");
        task.setTaskView(taskView);
        task.setDefaultSubmission("Default");
        UserTask userTask = userTask(student(2L, "Student", group), task, status("ABGEGEBEN"));

        TaskContent latestContent = new TaskContent();
        latestContent.setContent("Latest Content");
        latestContent.setVersion(2);

        when(userTaskService.findById(30L)).thenReturn(Optional.of(userTask));
        when(taskContentService.getLatestContent(userTask)).thenReturn(Optional.of(latestContent));

        Optional<TeacherSubmissionContentViewDto> contentViewOpt =
            teacherTaskQueryService.getSubmissionContentViewData(30L, null);

        assertThat(contentViewOpt).isPresent();
        TeacherSubmissionContentViewDto contentView = contentViewOpt.get();
        assertThat(contentView.getCurrentContent()).isEqualTo("Latest Content");
        assertThat(contentView.getVersion()).isEqualTo(2);
        assertThat(contentView.getTemplatePath()).isEqualTo("taskviews/custom-template");
    }

    @Test
    void getCreateTaskFormData_loadsEmptyTaskAndReferenceData() {
        Group group = group(10L, "10A");
        TaskView taskView = new TaskView();
        taskView.setId(5L);
        UnitTitle unitTitle = new UnitTitle("sql", "SQL", "desc", 10);

        when(taskViewService.findAllActive()).thenReturn(List.of(taskView));
        when(groupService.findAll()).thenReturn(List.of(group));
        when(unitTitleService.findAllActive()).thenReturn(List.of(unitTitle));

        TeacherTaskFormDataDto formData = teacherTaskQueryService.getCreateTaskFormData();

        assertThat(formData.getTask()).isNotNull();
        assertThat(formData.getTaskViews()).containsExactly(taskView);
        assertThat(formData.getGroups()).containsExactly(group);
        assertThat(formData.getUnitTitles()).containsExactly(unitTitle);
    }

    @Test
    void getEditTaskFormData_loadsExistingTaskAndReferenceData() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        Task task = task(20L, "Worksheet", teacher, group, null);
        TaskView taskView = new TaskView();
        taskView.setId(5L);
        UnitTitle unitTitle = new UnitTitle("sql", "SQL", "desc", 10);

        when(taskService.findById(20L)).thenReturn(Optional.of(task));
        when(taskViewService.findAllActive()).thenReturn(List.of(taskView));
        when(groupService.findAll()).thenReturn(List.of(group));
        when(unitTitleService.findAllActive()).thenReturn(List.of(unitTitle));

        Optional<TeacherTaskFormDataDto> formDataOpt = teacherTaskQueryService.getEditTaskFormData(20L);

        assertThat(formDataOpt).isPresent();
        TeacherTaskFormDataDto formData = formDataOpt.get();
        assertThat(formData.getTask()).isSameAs(task);
        assertThat(formData.getTaskViews()).containsExactly(taskView);
        assertThat(formData.getGroups()).containsExactly(group);
        assertThat(formData.getUnitTitles()).containsExactly(unitTitle);
    }

    private User teacher(Long id, String name, Group group) {
        User teacher = new User();
        teacher.setId(id);
        teacher.setName(name);
        teacher.setGroups(Set.of(group));
        return teacher;
    }

    private User student(Long id, String name, Group group) {
        User student = new User();
        student.setId(id);
        student.setName(name);
        student.setGroups(Set.of(group));
        return student;
    }

    private Group group(Long id, String name) {
        Group group = new Group(name);
        group.setId(id);
        return group;
    }

    private Task task(Long id, String title, User teacher, Group group, UnitTitle unitTitle) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setCreatedBy(teacher);
        task.setAssignedGroups(Set.of(group));
        task.setUnitTitle(unitTitle);
        return task;
    }

    private TaskStatus status(String name) {
        return new TaskStatus(name, name, 0);
    }

    private UserTask userTask(User student, Task task, TaskStatus status) {
        UserTask userTask = new UserTask();
        userTask.setId(30L);
        userTask.setUser(student);
        userTask.setTask(task);
        userTask.setStatus(status);
        return userTask;
    }
}
