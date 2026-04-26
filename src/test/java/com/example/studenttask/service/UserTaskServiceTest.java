package com.example.studenttask.service;

import com.example.studenttask.exception.TaskInvariantViolationException;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskStatusCode;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.repository.UserTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserTaskServiceTest {

    @Mock
    private UserTaskRepository userTaskRepository;

    @Mock
    private TaskStatusService taskStatusService;

    @InjectMocks
    private UserTaskService userTaskService;

    @Test
    void findOrCreateUserTask_returnsExistingUserTaskWithoutSaving() {
        User user = new User();
        Task task = new Task();
        UserTask existingUserTask = new UserTask();

        when(userTaskRepository.findByUserAndTask(user, task)).thenReturn(Optional.of(existingUserTask));

        UserTask result = userTaskService.findOrCreateUserTask(user, task);

        assertThat(result).isSameAs(existingUserTask);
        verify(userTaskRepository, never()).save(any(UserTask.class));
    }

    @Test
    void findOrCreateUserTask_createsAndPersistsANewUserTask() {
        User user = new User();
        Task task = new Task();
        TaskStatus defaultStatus = new TaskStatus("NICHT_BEGONNEN", "not started", 1);

        when(userTaskRepository.findByUserAndTask(user, task)).thenReturn(Optional.empty());
        when(taskStatusService.getDefaultStatus()).thenReturn(defaultStatus);
        when(userTaskRepository.save(any(UserTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserTask created = userTaskService.findOrCreateUserTask(user, task);

        assertThat(created.getUser()).isSameAs(user);
        assertThat(created.getTask()).isSameAs(task);
        assertThat(created.getStatus()).isSameAs(defaultStatus);
        assertThat(created.getStartedAt()).isNotNull();
        assertThat(created.getLastModified()).isNotNull();
        verify(userTaskRepository).save(created);
    }

    @Test
    void updateStatus_rejectsInvalidTransitionsWithoutSaving() {
        UserTask userTask = new UserTask();
        TaskStatus fromStatus = new TaskStatus("NICHT_BEGONNEN", "not started", 1);
        TaskStatus toStatus = new TaskStatus("ABGEGEBEN", "submitted", 3);
        LocalDateTime originalLastModified = userTask.getLastModified();
        userTask.setStatus(fromStatus);

        when(taskStatusService.canTransitionTo(fromStatus, toStatus)).thenReturn(false);

        boolean updated = userTaskService.updateStatus(userTask, toStatus);

        assertThat(updated).isFalse();
        assertThat(userTask.getStatus()).isSameAs(fromStatus);
        assertThat(userTask.getLastModified()).isEqualTo(originalLastModified);
        verify(userTaskRepository, never()).save(any(UserTask.class));
    }

    @Test
    void updateStatus_acceptsValidTransitionsAndInitializesStartedAt() {
        UserTask userTask = new UserTask();
        TaskStatus fromStatus = new TaskStatus("NICHT_BEGONNEN", "not started", 1);
        TaskStatus toStatus = new TaskStatus("IN_BEARBEITUNG", "in progress", 2);
        userTask.setStatus(fromStatus);
        userTask.setStartedAt(null);

        when(taskStatusService.canTransitionTo(fromStatus, toStatus)).thenReturn(true);
        when(taskStatusService.isStatus(toStatus, TaskStatusCode.IN_BEARBEITUNG)).thenReturn(true);

        boolean updated = userTaskService.updateStatus(userTask, toStatus);

        assertThat(updated).isTrue();
        assertThat(userTask.getStatus()).isSameAs(toStatus);
        assertThat(userTask.getStartedAt()).isNotNull();
        verify(userTaskRepository).save(userTask);
    }

    @Test
    void updateStatus_treatsMissingLegacyStatusAsDefaultForValidation() {
        UserTask userTask = new UserTask();
        TaskStatus defaultStatus = new TaskStatus("NICHT_BEGONNEN", "not started", 1);
        TaskStatus toStatus = new TaskStatus("IN_BEARBEITUNG", "in progress", 2);
        userTask.setStatus(null);
        userTask.setStartedAt(null);

        when(taskStatusService.getDefaultStatus()).thenReturn(defaultStatus);
        when(taskStatusService.canTransitionTo(defaultStatus, toStatus)).thenReturn(true);
        when(taskStatusService.isStatus(toStatus, TaskStatusCode.IN_BEARBEITUNG)).thenReturn(true);

        boolean updated = userTaskService.updateStatus(userTask, toStatus);

        assertThat(updated).isTrue();
        assertThat(userTask.getStatus()).isSameAs(toStatus);
        assertThat(userTask.getStartedAt()).isNotNull();
        verify(userTaskRepository).save(userTask);
    }

    @Test
    void updateStatus_allowsSameSubmittedStatusAsNoOpResubmitTransition() {
        UserTask userTask = new UserTask();
        TaskStatus submitted = new TaskStatus("ABGEGEBEN", "submitted", 3);
        userTask.setStatus(submitted);

        boolean updated = userTaskService.updateStatus(userTask, submitted);

        assertThat(updated).isTrue();
        assertThat(userTask.getStatus()).isSameAs(submitted);
        verify(userTaskRepository).save(userTask);
        verify(taskStatusService, never()).canTransitionTo(any(TaskStatus.class), any(TaskStatus.class));
    }

    @Test
    void updateStatus_allowsSameSubmittedStatusWhenCurrentStatusIsProxyLikeSubclass() {
        UserTask userTask = new UserTask();
        TaskStatus currentStatus = new ProxyLikeTaskStatus("ABGEGEBEN", "submitted", 3);
        TaskStatus newStatus = new TaskStatus("ABGEGEBEN", "submitted", 3);
        userTask.setStatus(currentStatus);

        boolean updated = userTaskService.updateStatus(userTask, newStatus);

        assertThat(updated).isTrue();
        assertThat(userTask.getStatus()).isSameAs(currentStatus);
        verify(userTaskRepository).save(userTask);
        verify(taskStatusService, never()).canTransitionTo(any(TaskStatus.class), any(TaskStatus.class));
    }

    @Test
    void delete_delegatesDirectlyToRepository() {
        UserTask userTask = new UserTask();
        userTask.setId(15L);

        userTaskService.delete(userTask);

        verify(userTaskRepository).delete(userTask);
    }

    @Test
    void save_rejectsDuplicateAssignmentForAnotherUserTask() {
        User user = new User();
        user.setId(1L);
        Task task = new Task();
        task.setId(2L);

        UserTask existingUserTask = new UserTask();
        existingUserTask.setId(10L);
        existingUserTask.setUser(user);
        existingUserTask.setTask(task);

        UserTask duplicateUserTask = new UserTask();
        duplicateUserTask.setId(11L);
        duplicateUserTask.setUser(user);
        duplicateUserTask.setTask(task);

        when(userTaskRepository.findByUserAndTask(user, task)).thenReturn(Optional.of(existingUserTask));

        assertThatThrownBy(() -> userTaskService.save(duplicateUserTask))
            .isInstanceOf(TaskInvariantViolationException.class)
            .hasMessage("UserTask exists already for this user and task");

        verify(userTaskRepository, never()).save(duplicateUserTask);
    }

    private static final class ProxyLikeTaskStatus extends TaskStatus {

        private ProxyLikeTaskStatus(String name, String description, Integer order) {
            super(name, description, order);
        }
    }
}
