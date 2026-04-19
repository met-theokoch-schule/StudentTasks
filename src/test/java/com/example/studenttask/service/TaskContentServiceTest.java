package com.example.studenttask.service;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskStatusCode;
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

    @Mock
    private UserTaskService userTaskService;

    @InjectMocks
    private TaskContentService taskContentService;

    @Test
    void saveContentDraft_advancesVersionAndMovesNotStartedTaskToInProgress() {
        UserTask userTask = new UserTask();
        userTask.setStatus(taskStatus("NICHT_BEGONNEN"));
        userTask.setStartedAt(null);

        TaskContent existingContent = new TaskContent();
        existingContent.setVersion(2);

        when(taskContentRepository.findByUserTaskOrderByVersionDesc(userTask)).thenReturn(List.of(existingContent));
        when(taskStatusService.isStatus(userTask.getStatus(), TaskStatusCode.NICHT_BEGONNEN)).thenReturn(true);
        when(userTaskService.updateStatus(userTask, TaskStatusCode.IN_BEARBEITUNG)).thenAnswer(invocation -> {
            userTask.setStartedAt(java.time.LocalDateTime.now());
            return true;
        });
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
        assertThat(userTask.getStartedAt()).isNotNull();
        verify(userTaskService).updateStatus(userTask, TaskStatusCode.IN_BEARBEITUNG);
        verify(submissionService, never()).createSubmission(any(UserTask.class), any(TaskContent.class));
        verify(userTaskRepository, never()).save(userTask);
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

        when(taskContentRepository.findByUserTaskOrderByVersionDesc(userTask)).thenReturn(List.of());
        when(userTaskService.updateStatus(userTask, TaskStatusCode.VOLLSTAENDIG)).thenReturn(true);
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
        verify(userTaskService).updateStatus(userTask, TaskStatusCode.VOLLSTAENDIG);
        verify(submissionService).createSubmission(userTask, saved);
        verify(userTaskRepository, never()).save(userTask);
    }

    private TaskStatus taskStatus(String name) {
        return new TaskStatus(name, name, 0);
    }
}
