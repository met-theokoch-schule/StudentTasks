package com.example.studenttask.service;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void save_normalizesLegacyViewTypeToTaskView() {
        TaskView legacyViewType = new TaskView();
        legacyViewType.setId(7L);

        Task task = new Task();
        setField(task, "viewType", legacyViewType);
        setField(task, "taskView", null);

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task savedTask = taskService.save(task);

        assertThat(savedTask.getTaskView()).isSameAs(legacyViewType);
        assertThat(savedTask.getViewType()).isSameAs(legacyViewType);
    }

    @Test
    void save_normalizesTaskViewToLegacyViewType() {
        TaskView taskView = new TaskView();
        taskView.setId(8L);

        Task task = new Task();
        task.setTaskView(taskView);

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task savedTask = taskService.save(task);

        assertThat(savedTask.getTaskView()).isSameAs(taskView);
        assertThat(savedTask.getViewType()).isSameAs(taskView);
    }

    @Test
    void backfillTaskViewRelations_normalizesAndPersistsMismatchedTasks() {
        TaskView legacyViewType = new TaskView();
        legacyViewType.setId(7L);

        Task missingTaskView = new Task();
        setField(missingTaskView, "viewType", legacyViewType);
        setField(missingTaskView, "taskView", null);

        when(taskRepository.findTasksWithMismatchedTaskViewRelation())
            .thenReturn(List.of(missingTaskView));
        when(taskRepository.saveAll(anyIterable())).thenAnswer(invocation -> invocation.getArgument(0));

        int backfilledTasks = taskService.backfillTaskViewRelations();

        assertThat(backfilledTasks).isEqualTo(1);
        assertThat(missingTaskView.getTaskView()).isSameAs(legacyViewType);
        assertThat(missingTaskView.getViewType()).isSameAs(legacyViewType);
        verify(taskRepository).saveAll(anyIterable());
    }

    private void setField(Task task, String fieldName, Object value) {
        try {
            var field = Task.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(task, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
