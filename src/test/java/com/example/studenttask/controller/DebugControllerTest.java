package com.example.studenttask.controller;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.AuthenticationService;
import com.example.studenttask.service.StudentTaskViewSupportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebugControllerTest {

    @Mock
    private StudentTaskViewSupportService studentTaskViewSupportService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private DebugController controller;

    @Test
    void viewSubmissionContent_showsRequestedVersionForAuthenticatedUser() {
        User user = user(1L, "Alice");
        Task task = task(7L, "Worksheet");
        UserTask userTask = new UserTask();
        TaskContent content = new TaskContent();
        content.setVersion(3);
        content.setContent("SELECT * FROM answers");
        content.setSavedAt(LocalDateTime.of(2026, 4, 20, 10, 15));
        content.setSubmitted(true);

        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(user));
        when(studentTaskViewSupportService.findTask(7L)).thenReturn(Optional.of(task));
        when(studentTaskViewSupportService.findExistingUserTask(user, task)).thenReturn(Optional.of(userTask));
        when(studentTaskViewSupportService.getRequestedContent(userTask, 3)).thenReturn(content);

        Model model = new ExtendedModelMap();
        String view = controller.viewSubmissionContent(7L, 3, model);

        assertThat(view).isEqualTo("debug/content-viewer");
        assertThat(model.getAttribute("taskId")).isEqualTo(7L);
        assertThat(model.getAttribute("taskTitle")).isEqualTo("Worksheet");
        assertThat(model.getAttribute("username")).isEqualTo("Alice");
        assertThat(model.getAttribute("content")).isEqualTo("SELECT * FROM answers");
        assertThat(model.getAttribute("version")).isEqualTo(3);
        assertThat(model.getAttribute("savedAt")).isEqualTo(content.getSavedAt());
        assertThat(model.getAttribute("isSubmitted")).isEqualTo(true);
    }

    @Test
    void viewSubmissionContent_returnsVersionErrorWhenContentIsMissing() {
        User user = user(1L, "Alice");
        Task task = task(7L, "Worksheet");
        UserTask userTask = new UserTask();

        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(user));
        when(studentTaskViewSupportService.findTask(7L)).thenReturn(Optional.of(task));
        when(studentTaskViewSupportService.findExistingUserTask(user, task)).thenReturn(Optional.of(userTask));
        when(studentTaskViewSupportService.getRequestedContent(userTask, 4)).thenReturn(null);

        Model model = new ExtendedModelMap();
        String view = controller.viewSubmissionContent(7L, 4, model);

        assertThat(view).isEqualTo("debug/content-viewer");
        assertThat(model.getAttribute("error")).isEqualTo("Version 4 f\u00FCr Task 7 nicht gefunden");
    }

    @Test
    void viewSubmissionContent_returnsLoginErrorWhenNoUserIsAuthenticated() {
        when(authenticationService.getCurrentUser()).thenReturn(Optional.empty());

        Model model = new ExtendedModelMap();
        String view = controller.viewSubmissionContent(7L, null, model);

        assertThat(view).isEqualTo("debug/content-viewer");
        assertThat(model.getAttribute("error")).isEqualTo("Kein Benutzer eingeloggt");
        verifyNoInteractions(studentTaskViewSupportService);
    }

    private User user(Long id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        return user;
    }

    private Task task(Long id, String title) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        return task;
    }
}
