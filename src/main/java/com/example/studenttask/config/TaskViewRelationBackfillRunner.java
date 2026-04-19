package com.example.studenttask.config;

import com.example.studenttask.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class TaskViewRelationBackfillRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TaskViewRelationBackfillRunner.class);

    @Autowired
    private TaskService taskService;

    @Override
    public void run(ApplicationArguments args) {
        int backfilledTasks = taskService.backfillTaskViewRelations();
        if (backfilledTasks > 0) {
            log.info("Backfilled taskView relation for {} existing tasks", backfilledTasks);
        }
    }
}
