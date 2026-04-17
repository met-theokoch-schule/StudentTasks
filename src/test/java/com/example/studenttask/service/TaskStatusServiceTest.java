package com.example.studenttask.service;

import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.repository.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskStatusServiceTest {

    @Mock
    private TaskStatusRepository taskStatusRepository;

    @InjectMocks
    private TaskStatusService taskStatusService;

    @Test
    void getDefaultStatus_returnsNichtBegonnenFromRepository() {
        TaskStatus notStarted = new TaskStatus("NICHT_BEGONNEN", "not started", 1);
        when(taskStatusRepository.findByName("NICHT_BEGONNEN")).thenReturn(Optional.of(notStarted));

        TaskStatus result = taskStatusService.getDefaultStatus();

        assertThat(result).isSameAs(notStarted);
    }

    @Test
    void isTransitionAllowed_reflectsCurrentWorkflowRules() {
        TaskStatus notStarted = new TaskStatus("NICHT_BEGONNEN", "not started", 1);
        TaskStatus inProgress = new TaskStatus("IN_BEARBEITUNG", "in progress", 2);
        TaskStatus complete = new TaskStatus("VOLLST\u00c4NDIG", "complete", 5);

        assertThat(taskStatusService.isTransitionAllowed(notStarted, inProgress)).isTrue();
        assertThat(taskStatusService.isTransitionAllowed(notStarted, complete)).isFalse();
    }

    @Test
    void getNextPossibleStatuses_queriesRepositoryWithAllowedTargetNames() {
        TaskStatus current = new TaskStatus("ABGEGEBEN", "submitted", 3);
        TaskStatus inProgress = new TaskStatus("IN_BEARBEITUNG", "in progress", 2);
        TaskStatus needsRework = new TaskStatus("\u00dcBERARBEITUNG_N\u00d6TIG", "rework", 4);
        TaskStatus complete = new TaskStatus("VOLLST\u00c4NDIG", "complete", 5);

        when(taskStatusRepository.findByNameInAndIsActiveTrue(anySet()))
                .thenReturn(List.of(inProgress, needsRework, complete));

        List<TaskStatus> result = taskStatusService.getNextPossibleStatuses(current);

        assertThat(result).containsExactly(inProgress, needsRework, complete);
        verify(taskStatusRepository).findByNameInAndIsActiveTrue(
                Set.of("IN_BEARBEITUNG", "\u00dcBERARBEITUNG_N\u00d6TIG", "VOLLST\u00c4NDIG"));
    }
}
