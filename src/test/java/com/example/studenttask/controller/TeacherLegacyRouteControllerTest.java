package com.example.studenttask.controller;

import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.User;
import com.example.studenttask.service.TeacherTaskCommandService;
import com.example.studenttask.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherLegacyRouteControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private TeacherTaskCommandService teacherTaskCommandService;

    @InjectMocks
    private TeacherLegacyRouteController controller;

    @Test
    void createTask_delegatesToTeacherTaskCommandService() {
        Group group = group(51L, "Q2");
        User teacher = teacher(1L, "Teacher", group);
        Task task = new Task();

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));

        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = controller.createTask(task, List.of(51L), 5L, "sql", redirectAttributes, principal("oidc-teacher"));

        assertThat(view).isEqualTo("redirect:/teacher/tasks");
        assertThat(redirectAttributes.getFlashAttributes().get("success")).isEqualTo("Aufgabe wurde erfolgreich erstellt.");
        verify(teacherTaskCommandService).createTask(task, teacher, "5", "sql", List.of(51L));
    }

    @Test
    void saveDraft_delegatesToTeacherTaskCommandService() {
        Group group = group(51L, "Q2");
        User teacher = teacher(1L, "Teacher", group);
        Task task = new Task();

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));

        var response = controller.saveDraft(task, List.of(51L), 5L, principal("oidc-teacher"));

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo("Entwurf erfolgreich gespeichert");
        assertThat(task.getIsActive()).isFalse();
        verify(teacherTaskCommandService).createTask(task, teacher, "5", null, List.of(51L));
    }

    @Test
    void deleteTask_usesTeacherScopedCommandServiceAndAddsSuccessFlash() {
        Group group = group(51L, "Q2");
        User teacher = teacher(1L, "Teacher", group);

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));

        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = controller.deleteTask(301L, redirectAttributes, principal("oidc-teacher"));

        assertThat(view).isEqualTo("redirect:/teacher/tasks");
        assertThat(redirectAttributes.getFlashAttributes().get("success")).isEqualTo("Aufgabe wurde erfolgreich gelöscht.");
        verify(teacherTaskCommandService).deleteTask(301L, teacher);
    }

    @Test
    void viewSubmissionContent_redirectsToCanonicalTeacherTaskRoute() {
        String view = controller.viewSubmissionContent(44L, 3);

        assertThat(view).isEqualTo("redirect:/teacher/submissions/44/view?version=3");
    }

    private Principal principal(String name) {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(name);
        return principal;
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
}
