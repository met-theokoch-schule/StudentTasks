package com.example.studenttask.service;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
}
