package com.example.studenttask.controller;

import com.example.studenttask.dto.TaskIframeViewDataDto;
import com.example.studenttask.dto.TaskIframeViewResultDto;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.TaskIframeQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskIframeQueryService taskIframeQueryService;

    @InjectMocks
    private TaskController controller;

    @Test
    void viewTaskIframe_usesTaskViewTemplateWhenPresent() {
        TaskView taskView = new TaskView();
        taskView.setId(7L);
        taskView.setTemplatePath("taskviews/task-view");

        Task task = new Task();
        task.setId(40L);
        task.setTaskView(taskView);

        User student = new User();
        student.setId(1L);

        UserTask userTask = new UserTask();
        userTask.setId(99L);
        userTask.setTask(task);
        userTask.setUser(student);

        when(taskIframeQueryService.getTaskIframeViewData(40L, "oidc-subject", false, null)).thenReturn(
            TaskIframeViewResultDto.view(
                new TaskIframeViewDataDto(task, taskView, userTask, "", task.getDescription(), false)
            )
        );

        Model model = new ExtendedModelMap();
        String view = controller.viewTaskIframe(40L, null, null, authentication("oidc-subject"), model);

        assertThat(view).isEqualTo("taskviews/task-view");
        assertThat(model.getAttribute("task")).isSameAs(task);
        assertThat(model.getAttribute("taskView")).isSameAs(taskView);
        assertThat(model.getAttribute("userTask")).isSameAs(userTask);
        assertThat(model.getAttribute("userTaskId")).isEqualTo(99L);
        assertThat(model.getAttribute("currentContent")).isEqualTo("");
    }

    @Test
    void viewTaskIframe_returnsRedirectProvidedByQueryService() {
        when(taskIframeQueryService.getTaskIframeViewData(40L, "oidc-subject", true, 3)).thenReturn(
            TaskIframeViewResultDto.redirect("redirect:/teacher/dashboard")
        );

        Model model = new ExtendedModelMap();
        String view = controller.viewTaskIframe(40L, 5L, 3, authentication("oidc-subject"), model);

        assertThat(view).isEqualTo("redirect:/teacher/dashboard");
    }

    private Authentication authentication(String name) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(name);
        return authentication;
    }
}
