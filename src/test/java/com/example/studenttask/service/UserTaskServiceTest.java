package com.example.studenttask.service;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatus;
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

        when(userTaskRepository.findByUserAndTask(user, task)).thenReturn(Optional.empty());
        when(userTaskRepository.save(any(UserTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserTask created = userTaskService.findOrCreateUserTask(user, task);

        assertThat(created.getUser()).isSameAs(user);
        assertThat(created.getTask()).isSameAs(task);
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

        boolean updated = userTaskService.updateStatus(userTask, toStatus);

        assertThat(updated).isTrue();
        assertThat(userTask.getStatus()).isSameAs(toStatus);
        assertThat(userTask.getStartedAt()).isNotNull();
        verify(userTaskRepository).save(userTask);
    }
}
