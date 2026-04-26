package com.example.studenttask.service;

import com.example.studenttask.exception.TaskStatusNotFoundException;
import com.example.studenttask.model.TaskStatusCode;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    void isTransitionAllowed_reflectsCurrentWorkflowRulesIncludingDirectSubmitPaths() {
        TaskStatus notStarted = new TaskStatus("NICHT_BEGONNEN", "not started", 1);
        TaskStatus inProgress = new TaskStatus("IN_BEARBEITUNG", "in progress", 2);
        TaskStatus submitted = new TaskStatus("ABGEGEBEN", "submitted", 3);
        TaskStatus complete = new TaskStatus("VOLLST\u00c4NDIG", "complete", 5);

        assertThat(taskStatusService.isTransitionAllowed(notStarted, inProgress)).isTrue();
        assertThat(taskStatusService.isTransitionAllowed(notStarted, submitted)).isTrue();
        assertThat(taskStatusService.isTransitionAllowed(notStarted, complete)).isTrue();
        assertThat(taskStatusService.isTransitionAllowed(complete, submitted)).isTrue();
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

    @Test
    void findByName_supportsEnumStyleAndPersistedNames() {
        TaskStatus needsRework = new TaskStatus("\u00dcBERARBEITUNG_N\u00d6TIG", "rework", 4);
        when(taskStatusRepository.findByName("\u00dcBERARBEITUNG_N\u00d6TIG")).thenReturn(Optional.of(needsRework));

        Optional<TaskStatus> result = taskStatusService.findByName(TaskStatusCode.UEBERARBEITUNG_NOETIG.name());

        assertThat(result).containsSame(needsRework);
    }

    @Test
    void requireStatus_throwsTaskStatusNotFoundExceptionWhenMissing() {
        when(taskStatusRepository.findByName("ABGEGEBEN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskStatusService.requireStatus(TaskStatusCode.ABGEGEBEN))
            .isInstanceOf(TaskStatusNotFoundException.class)
            .hasMessage("Status ABGEGEBEN not found");
    }
}
