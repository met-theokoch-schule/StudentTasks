package com.example.studenttask.service;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.repository.TaskContentRepository;
import com.example.studenttask.repository.UserTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskContentServiceTest {

    @Mock
    private TaskContentRepository taskContentRepository;

    @Mock
    private TaskStatusService taskStatusService;

    @Mock
    private SubmissionService submissionService;

    @Mock
    private UserTaskRepository userTaskRepository;

    @Mock
    private TaskReviewService taskReviewService;

    @InjectMocks
    private TaskContentService taskContentService;

    @Test
    void saveContentDraft_advancesVersionAndMovesNotStartedTaskToInProgress() {
        UserTask userTask = new UserTask();
        TaskStatus notStarted = taskStatus("NICHT_BEGONNEN");
        TaskStatus inProgress = taskStatus("IN_BEARBEITUNG");
        userTask.setStatus(notStarted);
        userTask.setStartedAt(null);

        TaskContent existingContent = new TaskContent();
        existingContent.setVersion(2);

        when(taskContentRepository.findByUserTaskOrderByVersionDesc(userTask)).thenReturn(List.of(existingContent));
        when(taskStatusService.findByName("IN_BEARBEITUNG")).thenReturn(Optional.of(inProgress));
        when(taskContentRepository.save(any(TaskContent.class))).thenAnswer(invocation -> {
            TaskContent savedContent = invocation.getArgument(0);
            savedContent.setId(42L);
            return savedContent;
        });

        TaskContent saved = taskContentService.saveContent(userTask, "draft-content", false);

        assertThat(saved.getId()).isEqualTo(42L);
        assertThat(saved.getVersion()).isEqualTo(3);
        assertThat(saved.isSubmitted()).isFalse();
        assertThat(saved.getContent()).isEqualTo("draft-content");
        assertThat(userTask.getStatus()).isSameAs(inProgress);
        assertThat(userTask.getStartedAt()).isNotNull();
        verify(submissionService, never()).createSubmission(any(UserTask.class), any(TaskContent.class));
        verify(userTaskRepository).save(userTask);
    }

    @Test
    void saveContentSubmit_marksAutoCompleteTasksAsCompleteAndCreatesSubmission() {
        TaskView taskView = new TaskView();
        taskView.setSubmitMarksComplete(true);

        Task task = new Task();
        task.setTaskView(taskView);

        UserTask userTask = new UserTask();
        userTask.setTask(task);
        userTask.setStatus(taskStatus("IN_BEARBEITUNG"));

        TaskStatus completed = taskStatus("VOLLST\u00c4NDIG");

        when(taskContentRepository.findByUserTaskOrderByVersionDesc(userTask)).thenReturn(List.of());
        when(taskStatusService.findByName("VOLLST\u00c4NDIG")).thenReturn(Optional.of(completed));
        when(taskContentRepository.save(any(TaskContent.class))).thenAnswer(invocation -> {
            TaskContent savedContent = invocation.getArgument(0);
            savedContent.setId(99L);
            return savedContent;
        });

        TaskContent saved = taskContentService.saveContent(userTask, "final-answer", true);

        assertThat(saved.getId()).isEqualTo(99L);
        assertThat(saved.getVersion()).isEqualTo(1);
        assertThat(saved.isSubmitted()).isTrue();
        assertThat(saved.getContent()).isEqualTo("final-answer");
        assertThat(userTask.getStatus()).isSameAs(completed);
        verify(submissionService).createSubmission(userTask, saved);
        verify(userTaskRepository).save(userTask);
    }

    private TaskStatus taskStatus(String name) {
        return new TaskStatus(name, name, 0);
    }
}
