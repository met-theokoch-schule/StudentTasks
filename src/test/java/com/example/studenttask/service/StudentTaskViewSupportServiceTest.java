package com.example.studenttask.service;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentTaskViewSupportServiceTest {

    @Mock
    private TaskService taskService;

    @Mock
    private UserTaskService userTaskService;

    @Mock
    private TaskContentService taskContentService;

    @Mock
    private TaskViewService taskViewService;

    @InjectMocks
    private StudentTaskViewSupportService studentTaskViewSupportService;

    @Test
    void findTask_delegatesToTaskService() {
        Task task = task(7L, "Task 7");
        when(taskService.findById(7L)).thenReturn(Optional.of(task));

        Optional<Task> result = studentTaskViewSupportService.findTask(7L);

        assertThat(result).contains(task);
    }

    @Test
    void findAssignedTask_returnsTaskWhenUserHasAccess() {
        User user = user(1L, "Student One");
        Task task = task(7L, "Task 7");

        when(taskService.findById(7L)).thenReturn(Optional.of(task));
        when(taskService.hasUserAccessToTask(user, task)).thenReturn(true);

        Optional<Task> result = studentTaskViewSupportService.findAssignedTask(user, 7L);

        assertThat(result).contains(task);
    }

    @Test
    void findAssignedTask_returnsEmptyWhenUserHasNoAccess() {
        User user = user(1L, "Student One");
        Task task = task(7L, "Task 7");

        when(taskService.findById(7L)).thenReturn(Optional.of(task));
        when(taskService.hasUserAccessToTask(user, task)).thenReturn(false);

        Optional<Task> result = studentTaskViewSupportService.findAssignedTask(user, 7L);

        assertThat(result).isEmpty();
    }

    @Test
    void findExistingUserTask_looksUpByUserAndTaskIds() {
        User user = user(1L, "Student One");
        Task task = task(7L, "Task 7");
        UserTask userTask = userTask(100L, user, task);

        when(userTaskService.findByUserIdAndTaskId(1L, 7L)).thenReturn(Optional.of(userTask));

        Optional<UserTask> result = studentTaskViewSupportService.findExistingUserTask(user, task);

        assertThat(result).contains(userTask);
    }

    @Test
    void findOrCreateUserTask_delegatesToUserTaskService() {
        User user = user(1L, "Student One");
        Task task = task(7L, "Task 7");
        UserTask userTask = userTask(100L, user, task);

        when(userTaskService.findOrCreateUserTask(user, task)).thenReturn(userTask);

        UserTask result = studentTaskViewSupportService.findOrCreateUserTask(user, task);

        assertThat(result).isSameAs(userTask);
        verify(userTaskService).findOrCreateUserTask(user, task);
    }

    @Test
    void getRequestedContent_returnsVersionSpecificContentWhenVersionIsProvided() {
        UserTask userTask = new UserTask();
        TaskContent versionContent = new TaskContent();
        versionContent.setVersion(3);
        versionContent.setContent("historic");

        when(taskContentService.getContentByVersion(userTask, 3)).thenReturn(versionContent);

        TaskContent result = studentTaskViewSupportService.getRequestedContent(userTask, 3);

        assertThat(result).isSameAs(versionContent);
    }

    @Test
    void getRequestedContent_returnsLatestContentWhenVersionIsMissing() {
        UserTask userTask = new UserTask();
        TaskContent latestContent = new TaskContent();
        latestContent.setVersion(4);
        latestContent.setContent("latest");

        when(taskContentService.getLatestContent(userTask)).thenReturn(Optional.of(latestContent));

        TaskContent result = studentTaskViewSupportService.getRequestedContent(userTask, null);

        assertThat(result).isSameAs(latestContent);
    }

    @Test
    void getRequestedContent_fallsBackToLatestContentWhenConfiguredAndVersionIsMissing() {
        UserTask userTask = new UserTask();
        TaskContent latestContent = new TaskContent();
        latestContent.setVersion(4);
        latestContent.setContent("latest");

        when(taskContentService.getContentByVersion(userTask, 3)).thenReturn(null);
        when(taskContentService.getLatestContent(userTask)).thenReturn(Optional.of(latestContent));

        TaskContent result = studentTaskViewSupportService.getRequestedContent(userTask, 3, true);

        assertThat(result).isSameAs(latestContent);
    }

    @Test
    void resolveCurrentContent_fallsBackToDefaultWhenBlankContentShouldFallback() {
        Task task = task(7L, "Task 7");
        task.setDefaultSubmission("Default content");
        TaskContent content = new TaskContent();
        content.setContent("   ");

        String result = studentTaskViewSupportService.resolveCurrentContent(task, content, true);

        assertThat(result).isEqualTo("Default content");
    }

    @Test
    void resolveCurrentContent_keepsBlankContentWhenFallbackIsDisabled() {
        Task task = task(7L, "Task 7");
        task.setDefaultSubmission("Default content");
        TaskContent content = new TaskContent();
        content.setContent("   ");

        String result = studentTaskViewSupportService.resolveCurrentContent(task, content, false);

        assertThat(result).isEqualTo("   ");
    }

    @Test
    void resolveTaskView_loadsFreshTaskViewByIdWhenAvailable() {
        Task task = task(7L, "Task 7");
        TaskView embeddedTaskView = taskView(5L, "embedded");
        TaskView resolvedTaskView = taskView(5L, "resolved");
        task.setTaskView(embeddedTaskView);

        when(taskViewService.findById(5L)).thenReturn(Optional.of(resolvedTaskView));

        TaskView result = studentTaskViewSupportService.resolveTaskView(task);

        assertThat(result).isSameAs(resolvedTaskView);
    }

    @Test
    void resolveTemplatePath_returnsResolvedTemplatePathWhenAvailable() {
        Task task = task(7L, "Task 7");
        TaskView embeddedTaskView = taskView(5L, "embedded");
        TaskView resolvedTaskView = taskView(5L, "taskviews/resolved-template");
        task.setTaskView(embeddedTaskView);

        when(taskViewService.findById(5L)).thenReturn(Optional.of(resolvedTaskView));

        String result = studentTaskViewSupportService.resolveTemplatePath(task, "taskviews/simple-text.html");

        assertThat(result).isEqualTo("taskviews/resolved-template");
    }

    @Test
    void resolveTemplatePath_returnsFallbackWhenResolvedTaskViewIsMissing() {
        Task task = task(7L, "Task 7");

        String result = studentTaskViewSupportService.resolveTemplatePath(task, "taskviews/simple-text.html");

        assertThat(result).isEqualTo("taskviews/simple-text.html");
    }

    @Test
    void hasRenderableTemplate_returnsFalseForBlankTemplatePath() {
        TaskView taskView = taskView(5L, " ");

        boolean result = studentTaskViewSupportService.hasRenderableTemplate(taskView);

        assertThat(result).isFalse();
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

    private TaskView taskView(Long id, String templatePath) {
        TaskView taskView = new TaskView();
        taskView.setId(id);
        taskView.setTemplatePath(templatePath);
        return taskView;
    }

    private UserTask userTask(Long id, User user, Task task) {
        UserTask userTask = new UserTask();
        userTask.setId(id);
        userTask.setUser(user);
        userTask.setTask(task);
        return userTask;
    }
}
