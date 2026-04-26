package com.example.studenttask.service;

import com.example.studenttask.exception.TaskNotFoundException;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void save_preservesAssignedTaskView() {
        TaskView taskView = new TaskView();
        taskView.setId(7L);

        Task task = new Task();
        task.setTaskView(taskView);

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task savedTask = taskService.save(task);

        assertThat(savedTask.getTaskView()).isSameAs(taskView);
    }

    @Test
    void save_keepsMissingTaskViewUntouched() {
        Task task = new Task();

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task savedTask = taskService.save(task);

        assertThat(savedTask.getTaskView()).isNull();
    }

    @Test
    void delete_delegatesDirectlyToRepository() {
        Task task = new Task();
        task.setId(11L);

        taskService.delete(task);

        verify(taskRepository).delete(task);
    }

    @Test
    void updateTask_throwsTaskNotFoundExceptionWhenTaskDoesNotExist() {
        when(taskRepository.findById(11L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(
            11L,
            "Updated",
            "Description",
            "Default",
            null,
            null,
            null
        ))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessage("Task not found with ID: 11");
    }
}
