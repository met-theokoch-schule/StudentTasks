package com.example.studenttask.service;

import com.example.studenttask.dto.StudentTaskHistoryDataDto;
import com.example.studenttask.dto.StudentTaskViewDataDto;
import com.example.studenttask.exception.StudentAccessDeniedException;
import com.example.studenttask.exception.StudentResourceNotFoundException;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentTaskQueryServiceTest {

    @Mock
    private StudentTaskViewSupportService studentTaskViewSupportService;

    @Mock
    private TaskContentService taskContentService;

    @Mock
    private TaskReviewService taskReviewService;

    @InjectMocks
    private StudentTaskQueryService studentTaskQueryService;

    @Test
    void getTaskViewData_usesSavedContentWhenPresent() {
        User student = user(1L, "Student One");
        Task task = task(101L, "Worksheet", group(11L, "10A"));
        TaskView taskView = taskView(5L, "taskviews/worksheet");
        task.setTaskView(taskView);
        task.setDefaultSubmission("Default content");

        UserTask userTask = userTask(student, task, status("IN_BEARBEITUNG"));
        userTask.setId(301L);

        TaskContent savedContent = new TaskContent();
        savedContent.setId(401L);
        savedContent.setVersion(3);
        savedContent.setContent("Saved content");

        when(studentTaskViewSupportService.findTask(101L)).thenReturn(Optional.of(task));
        when(studentTaskViewSupportService.findExistingUserTask(student, task)).thenReturn(Optional.of(userTask));
        when(studentTaskViewSupportService.getRequestedContent(userTask, null)).thenReturn(savedContent);
        when(studentTaskViewSupportService.resolveTaskView(task)).thenReturn(taskView);
        when(studentTaskViewSupportService.hasRenderableTemplate(taskView)).thenReturn(true);
        when(studentTaskViewSupportService.resolveCurrentContent(task, savedContent, true)).thenReturn("Saved content");

        StudentTaskViewDataDto viewData = studentTaskQueryService.getTaskViewData(student, 101L);

        assertThat(viewData.getTask()).isSameAs(task);
        assertThat(viewData.getUserTask()).isSameAs(userTask);
        assertThat(viewData.getTaskView()).isSameAs(taskView);
        assertThat(viewData.getCurrentContent()).isEqualTo("Saved content");
        assertThat(viewData.isHistoryView()).isFalse();
    }

    @Test
    void getTaskViewData_usesDefaultSubmissionWhenSavedContentIsBlank() {
        User student = user(1L, "Student One");
        Task task = task(102L, "Worksheet", group(11L, "10A"));
        task.setDefaultSubmission("Default content");
        TaskView taskView = taskView(5L, "taskviews/worksheet");
        task.setTaskView(taskView);

        UserTask userTask = userTask(student, task, status("IN_BEARBEITUNG"));
        userTask.setId(302L);

        TaskContent savedContent = new TaskContent();
        savedContent.setContent("   ");

        when(studentTaskViewSupportService.findTask(102L)).thenReturn(Optional.of(task));
        when(studentTaskViewSupportService.findExistingUserTask(student, task)).thenReturn(Optional.of(userTask));
        when(studentTaskViewSupportService.getRequestedContent(userTask, null)).thenReturn(savedContent);
        when(studentTaskViewSupportService.resolveTaskView(task)).thenReturn(taskView);
        when(studentTaskViewSupportService.hasRenderableTemplate(taskView)).thenReturn(true);
        when(studentTaskViewSupportService.resolveCurrentContent(task, savedContent, true))
            .thenReturn("Default content");

        StudentTaskViewDataDto viewData = studentTaskQueryService.getTaskViewData(student, 102L);

        assertThat(viewData.getCurrentContent()).isEqualTo("Default content");
    }

    @Test
    void getTaskViewData_throwsWhenTaskDoesNotExist() {
        User student = user(1L, "Student One");

        when(studentTaskViewSupportService.findTask(103L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentTaskQueryService.getTaskViewData(student, 103L))
            .isInstanceOf(StudentResourceNotFoundException.class)
            .hasMessage("Aufgabe nicht gefunden");
    }

    @Test
    void getTaskViewData_throwsWhenStudentHasNoUserTask() {
        User student = user(1L, "Student One");
        Task task = task(103L, "Worksheet", group(11L, "10A"));
        when(studentTaskViewSupportService.findTask(103L)).thenReturn(Optional.of(task));
        when(studentTaskViewSupportService.findExistingUserTask(student, task)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentTaskQueryService.getTaskViewData(student, 103L))
            .isInstanceOf(StudentAccessDeniedException.class)
            .hasMessage("Keine Berechtigung für diese Aufgabe");
    }

    @Test
    void getTaskViewData_throwsWhenTaskViewTemplateIsMissing() {
        User student = user(1L, "Student One");
        Task task = task(104L, "Worksheet", group(11L, "10A"));
        TaskView taskView = taskView(5L, " ");
        task.setTaskView(taskView);

        UserTask userTask = userTask(student, task, status("IN_BEARBEITUNG"));

        when(studentTaskViewSupportService.findTask(104L)).thenReturn(Optional.of(task));
        when(studentTaskViewSupportService.findExistingUserTask(student, task)).thenReturn(Optional.of(userTask));
        when(studentTaskViewSupportService.getRequestedContent(userTask, null)).thenReturn(null);
        when(studentTaskViewSupportService.resolveTaskView(task)).thenReturn(taskView);
        when(studentTaskViewSupportService.hasRenderableTemplate(taskView)).thenReturn(false);

        assertThatThrownBy(() -> studentTaskQueryService.getTaskViewData(student, 104L))
            .isInstanceOf(StudentResourceNotFoundException.class)
            .hasMessage("Aufgabenansicht nicht gefunden");
    }

    @Test
    void getTaskHistoryData_createsMissingUserTaskAndLoadsVersionsAndReviews() {
        User student = user(1L, "Student One");
        Group group = group(11L, "10A");
        student.setGroups(Set.of(group));

        Task task = task(201L, "History Task", group);
        UserTask userTask = userTask(student, task, status("NICHT_BEGONNEN"));
        TaskContent version = new TaskContent();
        version.setVersion(1);
        TaskReview review = new TaskReview();

        when(studentTaskViewSupportService.findAssignedTask(student, 201L)).thenReturn(Optional.of(task));
        when(studentTaskViewSupportService.findOrCreateUserTask(student, task)).thenReturn(userTask);
        when(taskContentService.getAllContentVersions(userTask)).thenReturn(List.of(version));
        when(taskReviewService.findByUserTaskOrderByReviewedAtDesc(userTask)).thenReturn(List.of(review));

        StudentTaskHistoryDataDto historyData = studentTaskQueryService.getTaskHistoryData(student, 201L);
        assertThat(historyData.getTask()).isSameAs(task);
        assertThat(historyData.getContentVersions()).containsExactly(version);
        assertThat(historyData.getReviews()).containsExactly(review);
        assertThat(historyData.getUserTask()).isSameAs(userTask);
    }

    @Test
    void getTaskHistoryData_throwsWhenStudentHasNoAccess() {
        User student = user(1L, "Student One");
        student.setGroups(Set.of(group(12L, "10B")));
        when(studentTaskViewSupportService.findAssignedTask(student, 202L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentTaskQueryService.getTaskHistoryData(student, 202L))
            .isInstanceOf(StudentResourceNotFoundException.class)
            .hasMessage("Aufgabe nicht gefunden");
    }

    @Test
    void getTaskVersionViewData_throwsWhenVersionDoesNotExist() {
        User student = user(1L, "Student One");
        Group group = group(11L, "10A");
        student.setGroups(Set.of(group));

        Task task = task(301L, "Version Task", group);
        task.setTaskView(taskView(8L, "taskviews/version-view"));

        UserTask userTask = userTask(student, task, status("ABGEGEBEN"));

        when(studentTaskViewSupportService.findAssignedTask(student, 301L)).thenReturn(Optional.of(task));
        when(studentTaskViewSupportService.findExistingUserTask(student, task)).thenReturn(Optional.of(userTask));
        when(studentTaskViewSupportService.resolveTaskView(task)).thenReturn(task.getTaskView());
        when(studentTaskViewSupportService.hasRenderableTemplate(task.getTaskView())).thenReturn(true);
        when(studentTaskViewSupportService.getRequestedContent(userTask, 4)).thenReturn(null);

        assertThatThrownBy(() -> studentTaskQueryService.getTaskVersionViewData(student, 301L, 4))
            .isInstanceOf(StudentResourceNotFoundException.class)
            .hasMessage("Version nicht gefunden");
    }

    @Test
    void getTaskVersionViewData_returnsViewDataForExistingVersion() {
        User student = user(1L, "Student One");
        Group group = group(11L, "10A");
        student.setGroups(Set.of(group));

        Task task = task(302L, "Version Task", group);
        TaskView taskView = taskView(8L, "taskviews/version-view");
        task.setTaskView(taskView);

        UserTask userTask = userTask(student, task, status("ABGEGEBEN"));

        TaskContent versionContent = new TaskContent();
        versionContent.setVersion(2);
        versionContent.setContent("Historic content");

        when(studentTaskViewSupportService.findAssignedTask(student, 302L)).thenReturn(Optional.of(task));
        when(studentTaskViewSupportService.findExistingUserTask(student, task)).thenReturn(Optional.of(userTask));
        when(studentTaskViewSupportService.resolveTaskView(task)).thenReturn(taskView);
        when(studentTaskViewSupportService.hasRenderableTemplate(taskView)).thenReturn(true);
        when(studentTaskViewSupportService.getRequestedContent(userTask, 2)).thenReturn(versionContent);

        StudentTaskViewDataDto viewData = studentTaskQueryService.getTaskVersionViewData(student, 302L, 2);

        assertThat(viewData.getTaskView()).isSameAs(taskView);
        assertThat(viewData.getCurrentContent()).isEqualTo("Historic content");
        assertThat(viewData.getViewingVersion()).isEqualTo(2);
        assertThat(viewData.isHistoryView()).isTrue();
    }

    @Test
    void getTaskVersionViewData_throwsWhenUserTaskDoesNotExist() {
        User student = user(1L, "Student One");
        Group group = group(11L, "10A");
        student.setGroups(Set.of(group));

        Task task = task(303L, "Version Task", group);
        task.setTaskView(taskView(8L, "taskviews/version-view"));

        when(studentTaskViewSupportService.findAssignedTask(student, 303L)).thenReturn(Optional.of(task));
        when(studentTaskViewSupportService.findExistingUserTask(student, task)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentTaskQueryService.getTaskVersionViewData(student, 303L, 2))
            .isInstanceOf(StudentResourceNotFoundException.class)
            .hasMessage("Version nicht gefunden");
    }

    @Test
    void getTaskVersionViewData_throwsWhenStudentHasNoTaskAccess() {
        User student = user(1L, "Student One");
        when(studentTaskViewSupportService.findAssignedTask(student, 304L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentTaskQueryService.getTaskVersionViewData(student, 304L, 2))
            .isInstanceOf(StudentResourceNotFoundException.class)
            .hasMessage("Aufgabe nicht gefunden");
    }

    @Test
    void getTaskVersionViewData_throwsWhenTaskViewTemplateIsMissing() {
        User student = user(1L, "Student One");
        Group group = group(11L, "10A");
        student.setGroups(Set.of(group));

        Task task = task(305L, "Version Task", group);
        TaskView taskView = taskView(8L, " ");
        task.setTaskView(taskView);

        UserTask userTask = userTask(student, task, status("ABGEGEBEN"));

        when(studentTaskViewSupportService.findAssignedTask(student, 305L)).thenReturn(Optional.of(task));
        when(studentTaskViewSupportService.findExistingUserTask(student, task)).thenReturn(Optional.of(userTask));
        when(studentTaskViewSupportService.resolveTaskView(task)).thenReturn(taskView);
        when(studentTaskViewSupportService.hasRenderableTemplate(taskView)).thenReturn(false);

        assertThatThrownBy(() -> studentTaskQueryService.getTaskVersionViewData(student, 305L, 2))
            .isInstanceOf(StudentResourceNotFoundException.class)
            .hasMessage("Aufgabenansicht nicht gefunden");
    }

    private User user(Long id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        return user;
    }

    private Group group(Long id, String name) {
        Group group = new Group(name);
        group.setId(id);
        return group;
    }

    private Task task(Long id, String title, Group group) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setAssignedGroups(Set.of(group));
        task.setIsActive(true);
        return task;
    }

    private TaskView taskView(Long id, String templatePath) {
        TaskView taskView = new TaskView();
        taskView.setId(id);
        taskView.setTemplatePath(templatePath);
        return taskView;
    }

    private TaskStatus status(String name) {
        return new TaskStatus(name, name, 0);
    }

    private UserTask userTask(User user, Task task, TaskStatus status) {
        UserTask userTask = new UserTask();
        userTask.setUser(user);
        userTask.setTask(task);
        userTask.setStatus(status);
        userTask.setStartedAt(LocalDateTime.now().minusMinutes(5));
        userTask.setLastModified(LocalDateTime.now());
        return userTask;
    }
}
