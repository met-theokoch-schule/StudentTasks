package com.example.studenttask.service;

import com.example.studenttask.dto.VersionWithSubmissionStatus;
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
import java.time.LocalDateTime;

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

    @Test
    void saveContentSubmit_usesTaskViewWhenPresent() {
        TaskView taskView = new TaskView();
        taskView.setSubmitMarksComplete(true);

        Task task = new Task();
        task.setTaskView(taskView);

        UserTask userTask = new UserTask();
        userTask.setTask(task);
        userTask.setStatus(taskStatus("IN_BEARBEITUNG"));

        when(taskContentRepository.findByUserTaskOrderByVersionDesc(userTask)).thenReturn(List.of());
        when(userTaskService.updateStatus(userTask, TaskStatusCode.VOLLSTAENDIG)).thenReturn(true);
        when(taskContentRepository.save(any(TaskContent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskContent saved = taskContentService.saveContent(userTask, "final-answer", true);

        assertThat(saved.isSubmitted()).isTrue();
        verify(userTaskService).updateStatus(userTask, TaskStatusCode.VOLLSTAENDIG);
    }

    @Test
    void getVersionsWithSubmissionStatus_marksReviewedAndPendingSubmittedVersions() {
        UserTask userTask = new UserTask();

        TaskContent reviewedVersion = new TaskContent();
        reviewedVersion.setUserTask(userTask);
        reviewedVersion.setVersion(3);
        reviewedVersion.setSavedAt(LocalDateTime.of(2026, 4, 20, 9, 30));
        reviewedVersion.setSubmitted(true);

        TaskContent pendingVersion = new TaskContent();
        pendingVersion.setUserTask(userTask);
        pendingVersion.setVersion(2);
        pendingVersion.setSavedAt(LocalDateTime.of(2026, 4, 19, 8, 0));
        pendingVersion.setSubmitted(true);

        TaskContent draftVersion = new TaskContent();
        draftVersion.setUserTask(userTask);
        draftVersion.setVersion(1);
        draftVersion.setSavedAt(LocalDateTime.of(2026, 4, 18, 7, 15));
        draftVersion.setSubmitted(false);

        when(taskContentRepository.findByUserTaskOrderByVersionDesc(userTask))
            .thenReturn(List.of(reviewedVersion, pendingVersion, draftVersion));
        when(taskReviewService.hasReviewsForVersion(userTask, 3)).thenReturn(true);
        when(taskReviewService.hasReviewsForVersion(userTask, 2)).thenReturn(false);

        List<VersionWithSubmissionStatus> versions =
            taskContentService.getVersionsWithSubmissionStatus(userTask);

        assertThat(versions).hasSize(3);
        assertThat(versions.get(0).getVersion()).isEqualTo(3);
        assertThat(versions.get(0).getIsSubmitted()).isTrue();
        assertThat(versions.get(0).getDisplayText()).isEqualTo("v3 20.04.26 09:30 👁");
        assertThat(versions.get(1).getVersion()).isEqualTo(2);
        assertThat(versions.get(1).getIsSubmitted()).isTrue();
        assertThat(versions.get(1).getDisplayText()).isEqualTo("v2 19.04.26 08:00 ⏳");
        assertThat(versions.get(2).getVersion()).isEqualTo(1);
        assertThat(versions.get(2).getIsSubmitted()).isFalse();
        assertThat(versions.get(2).getDisplayText()).isEqualTo("v1 18.04.26 07:15");
    }

    private TaskStatus taskStatus(String name) {
        return new TaskStatus(name, name, 0);
    }
}
