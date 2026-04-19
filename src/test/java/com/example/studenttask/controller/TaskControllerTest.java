package com.example.studenttask.controller;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.TaskContentService;
import com.example.studenttask.service.TaskService;
import com.example.studenttask.service.TaskViewService;
import com.example.studenttask.service.UserService;
import com.example.studenttask.service.UserTaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private UserTaskService userTaskService;

    @Mock
    private TaskContentService taskContentService;

    @Mock
    private UserService userService;

    @Mock
    private TaskViewService taskViewService;

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

        when(taskService.findById(40L)).thenReturn(Optional.of(task));
        when(userService.findByOpenIdSubject("oidc-subject")).thenReturn(Optional.of(student));
        when(userTaskService.findOrCreateUserTask(student, task)).thenReturn(userTask);
        when(taskContentService.getLatestContent(userTask)).thenReturn(Optional.empty());
        when(taskViewService.findById(7L)).thenReturn(Optional.of(taskView));

        Model model = new ExtendedModelMap();
        String view = controller.viewTaskIframe(40L, null, null, authentication("oidc-subject"), model);

        assertThat(view).isEqualTo("taskviews/task-view");
        assertThat(model.getAttribute("task")).isSameAs(task);
        assertThat(model.getAttribute("taskView")).isSameAs(taskView);
        assertThat(model.getAttribute("userTask")).isSameAs(userTask);
        assertThat(model.getAttribute("userTaskId")).isEqualTo(99L);
        assertThat(model.getAttribute("currentContent")).isEqualTo("");
    }

    private Authentication authentication(String name) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(name);
        return authentication;
    }
}
