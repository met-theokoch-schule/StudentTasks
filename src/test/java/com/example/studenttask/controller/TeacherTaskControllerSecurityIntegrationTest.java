package com.example.studenttask.controller;

import com.example.studenttask.config.OAuthConfigurationGuardFilter;
import com.example.studenttask.config.OAuthConfigurationStatusService;
import com.example.studenttask.config.SecurityConfig;
import com.example.studenttask.dto.TeacherTaskFormDataDto;
import com.example.studenttask.dto.TeacherTaskFormDto;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.service.IdentitySyncService;
import com.example.studenttask.service.RedirectTargetService;
import com.example.studenttask.service.TeacherIdentityService;
import com.example.studenttask.service.TeacherTaskCommandService;
import com.example.studenttask.service.TeacherTaskQueryService;
import com.example.studenttask.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = TeacherTaskController.class,
    properties = {
        "spring.security.oauth2.client.registration.iserv.client-id=test-id",
        "spring.security.oauth2.client.registration.iserv.client-secret=test-secret",
        "spring.security.oauth2.client.registration.iserv.redirect-uri=http://localhost/login/oauth2/code/iserv"
    }
)
@Import({SecurityConfig.class, OAuthConfigurationGuardFilter.class})
class TeacherTaskControllerSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherIdentityService teacherIdentityService;

    @MockBean
    private TeacherTaskQueryService teacherTaskQueryService;

    @MockBean
    private TeacherTaskCommandService teacherTaskCommandService;

    @MockBean
    private RedirectTargetService redirectTargetService;

    @MockBean
    private UserService userService;

    @MockBean
    private IdentitySyncService identitySyncService;

    @MockBean
    private OAuthConfigurationStatusService oauthConfigurationStatusService;

    @Test
    void showCreateTaskForm_rendersCsrfToken() throws Exception {
        when(teacherTaskQueryService.getCreateTaskFormData()).thenReturn(new TeacherTaskFormDataDto(
            null,
            new TeacherTaskFormDto(),
            List.of(taskView(5L, "Editor")),
            List.of(group(10L, "10A")),
            List.of(new UnitTitle("sql", "SQL", "desc", 10))
        ));

        mockMvc.perform(get("/teacher/tasks/create")
                .with(oauth2Login().authorities(() -> "ROLE_TEACHER")))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("_csrf")));
    }

    @Test
    void showEditTaskForm_rendersCsrfToken() throws Exception {
        Task task = task(20L, "Worksheet");
        TaskView taskView = taskView(5L, "Editor");
        task.setTaskView(taskView);
        TeacherTaskFormDto taskForm = new TeacherTaskFormDto();
        taskForm.setTitle("Worksheet");

        when(teacherTaskQueryService.getEditTaskFormData(20L))
            .thenReturn(Optional.of(new TeacherTaskFormDataDto(
                task,
                taskForm,
                List.of(taskView),
                List.of(group(10L, "10A")),
                List.of(new UnitTitle("sql", "SQL", "desc", 10))
            )));

        mockMvc.perform(get("/teacher/tasks/20/edit")
                .with(oauth2Login().attributes(attrs -> attrs.put("sub", "oidc-teacher")).authorities(() -> "ROLE_TEACHER")))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("_csrf")));
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
        taskView.setTemplatePath("taskviews/editor.html");
        return taskView;
    }

    private Task task(Long id, String title) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        return task;
    }
}
