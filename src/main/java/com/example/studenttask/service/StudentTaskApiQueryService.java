package com.example.studenttask.service;

import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.UserTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentTaskApiQueryService {

    private static final Logger log = LoggerFactory.getLogger(StudentTaskApiQueryService.class);

    @Autowired
    private StudentTaskApiAccessService studentTaskApiAccessService;

    @Autowired
    private TaskContentService taskContentService;

    public String getTaskContent(Long taskId, String openIdSubject) {
        log.debug("Loading task content for task {} and user {}", taskId, openIdSubject);

        Optional<UserTask> userTaskOpt = studentTaskApiAccessService.findUserTask(taskId, openIdSubject);
        if (userTaskOpt.isEmpty()) {
            log.debug("No UserTask found for task {} and user {}", taskId, openIdSubject);
            return "";
        }
        UserTask userTask = userTaskOpt.get();
        log.debug("Found UserTask {}", userTask.getId());

        Optional<TaskContent> latestContent = taskContentService.getLatestContent(userTask);
        String content = "";
        if (latestContent.isPresent()) {
            content = latestContent.get().getContent();
            log.debug("Found latest content for UserTask {} with version {} and length {}",
                userTask.getId(),
                latestContent.get().getVersion(),
                content != null ? content.length() : null);
            log.debug("Content preview: {}", preview(content, 50));
        } else {
            log.debug("No content found for UserTask {}", userTask.getId());
        }

        return content != null ? content : "";
    }

    private String preview(String content, int maxLength) {
        if (content == null) {
            return null;
        }
        return content.length() > maxLength ? content.substring(0, maxLength) + "..." : content;
    }
}
