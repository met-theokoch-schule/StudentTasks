package com.example.studenttask.config;

import com.example.studenttask.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskViewRelationBackfillRunnerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskViewRelationBackfillRunner runner;

    @Test
    void run_delegatesTaskViewBackfillToTaskService() throws Exception {
        when(taskService.backfillTaskViewRelations()).thenReturn(3);

        runner.run(new DefaultApplicationArguments(new String[0]));

        verify(taskService).backfillTaskViewRelations();
    }
}
