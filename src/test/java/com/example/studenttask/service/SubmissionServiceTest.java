package com.example.studenttask.service;

import com.example.studenttask.model.Submission;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.repository.SubmissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @InjectMocks
    private SubmissionService submissionService;

    @Test
    void createSubmission_persistsSubmissionForTaskContentVersion() {
        UserTask userTask = new UserTask();
        userTask.setId(30L);

        TaskContent taskContent = new TaskContent();
        taskContent.setId(40L);
        taskContent.setVersion(3);

        when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> {
            Submission submission = invocation.getArgument(0);
            submission.setId(99L);
            return submission;
        });

        Submission savedSubmission = submissionService.createSubmission(userTask, taskContent);

        assertThat(savedSubmission.getId()).isEqualTo(99L);
        assertThat(savedSubmission.getUserTask()).isSameAs(userTask);
        assertThat(savedSubmission.getTaskContent()).isSameAs(taskContent);
        assertThat(savedSubmission.getVersion()).isEqualTo(3);
        assertThat(savedSubmission.getSubmittedAt()).isNotNull();

        ArgumentCaptor<Submission> submissionCaptor = ArgumentCaptor.forClass(Submission.class);
        verify(submissionRepository).save(submissionCaptor.capture());
        assertThat(submissionCaptor.getValue().getUserTask()).isSameAs(userTask);
        assertThat(submissionCaptor.getValue().getTaskContent()).isSameAs(taskContent);
        assertThat(submissionCaptor.getValue().getVersion()).isEqualTo(3);
    }
}
