package com.example.studenttask.service;

import com.example.studenttask.exception.TaskContentVersionNotFoundException;
import com.example.studenttask.exception.TaskStatusNotFoundException;
import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskStatusCode;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.repository.TaskContentRepository;
import com.example.studenttask.repository.TaskReviewRepository;
import com.example.studenttask.repository.UserTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskReviewServiceTest {

    @Mock
    private TaskReviewRepository taskReviewRepository;

    @Mock
    private TaskContentRepository taskContentRepository;

    @Mock
    private UserTaskRepository userTaskRepository;

    @Mock
    private TaskStatusService taskStatusService;

    @InjectMocks
    private TaskReviewService taskReviewService;

    @Test
    void createReview_updatesUserTaskAndStoresRequestedVersion() {
        UserTask userTask = new UserTask();
        userTask.setId(30L);

        User reviewer = new User();
        reviewer.setId(5L);

        TaskStatus status = new TaskStatus("VOLLSTAENDIG", "VOLLSTAENDIG", 0);
        status.setId(7L);

        when(taskContentRepository.existsByUserTaskAndVersion(userTask, 2)).thenReturn(true);
        when(taskStatusService.findById(7L)).thenReturn(Optional.of(status));
        when(taskReviewRepository.save(any(TaskReview.class))).thenAnswer(invocation -> {
            TaskReview review = invocation.getArgument(0);
            review.setId(99L);
            return review;
        });

        TaskReview savedReview = taskReviewService.createReview(userTask, reviewer, 7L, "Gut gemacht", 2);

        assertThat(savedReview.getId()).isEqualTo(99L);
        assertThat(savedReview.getVersion()).isEqualTo(2);
        assertThat(savedReview.getStatus()).isSameAs(status);
        assertThat(savedReview.getReviewer()).isSameAs(reviewer);
        assertThat(savedReview.getUserTask()).isSameAs(userTask);
        assertThat(savedReview.getComment()).isEqualTo("Gut gemacht");
        assertThat(userTask.getStatus()).isSameAs(status);
        assertThat(userTask.getLastModified()).isNotNull();

        ArgumentCaptor<TaskReview> reviewCaptor = ArgumentCaptor.forClass(TaskReview.class);
        verify(taskReviewRepository).save(reviewCaptor.capture());
        assertThat(reviewCaptor.getValue().getVersion()).isEqualTo(2);
        verify(userTaskRepository).save(userTask);
    }

    @Test
    void getTeacherReviewStatuses_returnsCompleteAndNeedsReworkStatuses() {
        TaskStatus complete = new TaskStatus("VOLLSTAENDIG", "VOLLSTAENDIG", 0);
        TaskStatus needsRework = new TaskStatus("UEBERARBEITUNG_NOETIG", "UEBERARBEITUNG_NOETIG", 1);

        when(taskStatusService.findByCode(TaskStatusCode.VOLLSTAENDIG)).thenReturn(Optional.of(complete));
        when(taskStatusService.findByCode(TaskStatusCode.UEBERARBEITUNG_NOETIG)).thenReturn(Optional.of(needsRework));

        assertThat(taskReviewService.getTeacherReviewStatuses()).containsExactly(complete, needsRework);
    }

    @Test
    void createReview_rejectsUnknownRequestedVersion() {
        UserTask userTask = new UserTask();
        userTask.setId(30L);

        User reviewer = new User();
        reviewer.setId(5L);

        TaskStatus status = new TaskStatus("VOLLSTAENDIG", "VOLLSTAENDIG", 0);
        status.setId(7L);

        when(taskStatusService.findById(7L)).thenReturn(Optional.of(status));
        when(taskContentRepository.existsByUserTaskAndVersion(userTask, 4)).thenReturn(false);

        assertThatThrownBy(() -> taskReviewService.createReview(userTask, reviewer, 7L, "Kommentar", 4))
            .isInstanceOf(TaskContentVersionNotFoundException.class)
            .hasMessage("Abgabeversion nicht gefunden");

        verify(taskReviewRepository, never()).save(any(TaskReview.class));
        verify(userTaskRepository, never()).save(any(UserTask.class));
    }

    @Test
    void createReview_throwsTaskStatusNotFoundExceptionWhenStatusIsMissing() {
        UserTask userTask = new UserTask();
        userTask.setId(30L);

        User reviewer = new User();
        reviewer.setId(5L);

        when(taskStatusService.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskReviewService.createReview(userTask, reviewer, 7L, "Kommentar", null))
            .isInstanceOf(TaskStatusNotFoundException.class)
            .hasMessage("Status not found");

        verify(taskReviewRepository, never()).save(any(TaskReview.class));
        verify(userTaskRepository, never()).save(any(UserTask.class));
    }
}
