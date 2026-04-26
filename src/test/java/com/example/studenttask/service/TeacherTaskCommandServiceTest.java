package com.example.studenttask.service;

import com.example.studenttask.dto.TeacherTaskFormDto;
import com.example.studenttask.exception.TeacherAccessDeniedException;
import com.example.studenttask.exception.TeacherAuthenticationRequiredException;
import com.example.studenttask.exception.TeacherResourceNotFoundException;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherTaskCommandServiceTest {

    @Mock
    private TaskService taskService;

    @Mock
    private GroupService groupService;

    @Mock
    private TaskViewService taskViewService;

    @Mock
    private UnitTitleService unitTitleService;

    @Mock
    private UserService userService;

    @Mock
    private UserTaskService userTaskService;

    @Mock
    private TaskReviewService taskReviewService;

    @InjectMocks
    private TeacherTaskCommandService teacherTaskCommandService;

    @Test
    void createTask_assignsTeacherGroupsTaskViewAndUnitTitle() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        TaskView taskView = taskView(5L, "Editor");
        UnitTitle unitTitle = new UnitTitle("sql", "SQL", "desc", 10);
        TeacherTaskFormDto taskForm = new TeacherTaskFormDto();
        taskForm.setTitle("Worksheet");
        taskForm.setTaskViewId(5L);
        taskForm.setUnitTitleId("sql");
        taskForm.setSelectedGroups(List.of(10L));

        when(groupService.findAllById(List.of(10L))).thenReturn(List.of(group));
        when(taskViewService.findById(5L)).thenReturn(Optional.of(taskView));
        when(unitTitleService.findById("sql")).thenReturn(unitTitle);
        when(taskService.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task savedTask = teacherTaskCommandService.createTask(teacher, taskForm);

        assertThat(savedTask.getCreatedBy()).isSameAs(teacher);
        assertThat(savedTask.getTitle()).isEqualTo("Worksheet");
        assertThat(savedTask.getAssignedGroups()).containsExactly(group);
        assertThat(savedTask.getTaskView()).isSameAs(taskView);
        assertThat(savedTask.getUnitTitle()).isSameAs(unitTitle);
    }

    @Test
    void updateTask_updatesFieldsAndClearsUnitTitleWhenBlank() {
        Group oldGroup = group(10L, "10A");
        Group newGroup = group(11L, "10B");
        User teacher = teacher(1L, "Teacher", oldGroup);
        TaskView taskView = taskView(9L, "Canvas");
        Task existingTask = task(20L, "Old", teacher, oldGroup, new UnitTitle("old", "Old", "desc", 10));
        existingTask.setDescription("Old Description");
        existingTask.setDefaultSubmission("Old Draft");
        existingTask.setTutorial("Old Tutorial");
        existingTask.setIsActive(true);

        TeacherTaskFormDto updates = new TeacherTaskFormDto();
        updates.setTitle("New");
        updates.setDescription("New Description");
        updates.setDefaultSubmission("New Draft");
        updates.setTutorial("New Tutorial");
        updates.setIsActive(false);
        updates.setTaskViewId(9L);
        updates.setUnitTitleId("");
        updates.setSelectedGroups(List.of(11L));

        when(taskService.findById(20L)).thenReturn(Optional.of(existingTask));
        when(groupService.findAllById(List.of(11L))).thenReturn(List.of(newGroup));
        when(taskViewService.findById(9L)).thenReturn(Optional.of(taskView));
        when(taskService.save(existingTask)).thenReturn(existingTask);

        Task savedTask = teacherTaskCommandService.updateTask(20L, updates);

        assertThat(savedTask).isSameAs(existingTask);
        assertThat(existingTask.getTitle()).isEqualTo("New");
        assertThat(existingTask.getDescription()).isEqualTo("New Description");
        assertThat(existingTask.getDefaultSubmission()).isEqualTo("New Draft");
        assertThat(existingTask.getTutorial()).isEqualTo("New Tutorial");
        assertThat(existingTask.getIsActive()).isFalse();
        assertThat(existingTask.getAssignedGroups()).containsExactly(newGroup);
        assertThat(existingTask.getTaskView()).isSameAs(taskView);
        assertThat(existingTask.getUnitTitle()).isNull();
    }

    @Test
    void updateTask_throwsNotFoundWhenTaskViewDoesNotExist() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        TaskView existingTaskView = taskView(5L, "Existing");
        Task existingTask = task(20L, "Task", teacher, group, null);
        existingTask.setTaskView(existingTaskView);

        TeacherTaskFormDto updates = new TeacherTaskFormDto();
        updates.setTitle("Task");
        updates.setDescription("Desc");
        updates.setDefaultSubmission("Draft");
        updates.setIsActive(true);
        updates.setTaskViewId(999L);

        when(taskService.findById(20L)).thenReturn(Optional.of(existingTask));

        assertThatThrownBy(() -> teacherTaskCommandService.updateTask(20L, updates))
            .isInstanceOf(TeacherResourceNotFoundException.class)
            .hasMessage("Aufgabentyp nicht gefunden");
        assertThat(existingTask.getTaskView()).isSameAs(existingTaskView);
    }

    @Test
    void createTask_throwsNotFoundWhenSelectedGroupDoesNotExist() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        TeacherTaskFormDto taskForm = new TeacherTaskFormDto();
        taskForm.setTitle("Worksheet");
        taskForm.setSelectedGroups(List.of(10L, 11L));

        when(groupService.findAllById(List.of(10L, 11L))).thenReturn(List.of(group));

        assertThatThrownBy(() -> teacherTaskCommandService.createTask(teacher, taskForm))
            .isInstanceOf(TeacherResourceNotFoundException.class)
            .hasMessage("Eine oder mehrere Gruppen wurden nicht gefunden");
    }

    @Test
    void updateTask_throwsNotFoundWhenUnitTitleDoesNotExist() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        Task existingTask = task(20L, "Task", teacher, group, null);
        TeacherTaskFormDto updates = new TeacherTaskFormDto();
        updates.setTitle("Task");
        updates.setUnitTitleId("missing");
        updates.setIsActive(true);

        when(taskService.findById(20L)).thenReturn(Optional.of(existingTask));
        when(unitTitleService.findById("missing")).thenReturn(null);

        assertThatThrownBy(() -> teacherTaskCommandService.updateTask(20L, updates))
            .isInstanceOf(TeacherResourceNotFoundException.class)
            .hasMessage("Thema nicht gefunden");
    }

    @Test
    void deleteTask_withTeacher_deletesOwnTask() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        Task task = task(20L, "Task", teacher, group, null);

        when(taskService.findById(20L)).thenReturn(Optional.of(task));

        teacherTaskCommandService.deleteTask(20L, teacher);

        verify(taskService).delete(task);
    }

    @Test
    void deleteTask_withTeacher_rejectsForeignTask() {
        Group group = group(10L, "10A");
        User owner = teacher(1L, "Owner", group);
        User otherTeacher = teacher(2L, "Other", group);
        Task task = task(20L, "Task", owner, group, null);

        when(taskService.findById(20L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> teacherTaskCommandService.deleteTask(20L, otherTeacher))
            .isInstanceOf(TeacherAccessDeniedException.class)
            .hasMessage("Zugriff auf diese Aufgabe verweigert");
    }

    @Test
    void submitReview_createsReviewAndPersistsUserTask() {
        Group group = group(10L, "10A");
        User reviewer = teacher(1L, "Teacher", group);
        Task task = task(20L, "Task", reviewer, group, null);
        UserTask userTask = new UserTask();
        userTask.setId(30L);
        userTask.setTask(task);

        when(userTaskService.findById(30L)).thenReturn(Optional.of(userTask));
        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(reviewer));

        teacherTaskCommandService.submitReview(
            30L,
            "oidc-teacher",
            7L,
            "Gut gemacht",
            "2"
        );

        verify(taskReviewService).createReview(userTask, reviewer, 7L, "Gut gemacht", 2);
        verify(userTaskService).save(userTask);
    }

    @Test
    void submitReview_throwsNotFoundWhenUserTaskDoesNotExist() {
        when(userTaskService.findById(30L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherTaskCommandService.submitReview(
            30L,
            "oidc-teacher",
            7L,
            "Kommentar",
            null
        ))
            .isInstanceOf(TeacherResourceNotFoundException.class)
            .hasMessage("Abgabe nicht gefunden");
    }

    @Test
    void submitReview_throwsAuthenticationExceptionWhenReviewerDoesNotExist() {
        Group group = group(10L, "10A");
        User reviewer = teacher(1L, "Teacher", group);
        Task task = task(20L, "Task", reviewer, group, null);
        UserTask userTask = new UserTask();
        userTask.setId(30L);
        userTask.setTask(task);

        when(userTaskService.findById(30L)).thenReturn(Optional.of(userTask));
        when(userService.findByOpenIdSubject("missing-teacher")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherTaskCommandService.submitReview(
            30L,
            "missing-teacher",
            7L,
            "Kommentar",
            null
        ))
            .isInstanceOf(TeacherAuthenticationRequiredException.class)
            .hasMessage("Benutzer nicht gefunden");
    }

    private User teacher(Long id, String name, Group group) {
        User teacher = new User();
        teacher.setId(id);
        teacher.setName(name);
        teacher.setGroups(Set.of(group));
        return teacher;
    }

    private Group group(Long id, String name) {
        Group group = new Group(name);
        group.setId(id);
        return group;
    }

    private TaskView taskView(Long id, String name) {
        TaskView taskView = new TaskView();
        taskView.setId(id);
        taskView.setName(name);
        return taskView;
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
}
