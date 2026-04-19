package com.example.studenttask.service;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentTaskViewSupportService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskContentService taskContentService;

    @Autowired
    private TaskViewService taskViewService;

    public Optional<Task> findTask(Long taskId) {
        return taskService.findById(taskId);
    }

    public Optional<UserTask> findExistingUserTask(User user, Task task) {
        return userTaskService.findByUserIdAndTaskId(user.getId(), task.getId());
    }

    public UserTask findOrCreateUserTask(User user, Task task) {
        return userTaskService.findOrCreateUserTask(user, task);
    }

    public TaskContent getRequestedContent(UserTask userTask, Integer version) {
        if (version != null) {
            return taskContentService.getContentByVersion(userTask, version);
        }
        return taskContentService.getLatestContent(userTask).orElse(null);
    }

    public String resolveCurrentContent(Task task, TaskContent content, boolean blankContentFallsBackToDefault) {
        if (content != null && content.getContent() != null) {
            if (!blankContentFallsBackToDefault || !content.getContent().trim().isEmpty()) {
                return content.getContent();
            }
        }

        return task.getDefaultSubmission() != null ? task.getDefaultSubmission() : "";
    }

    public TaskView resolveTaskView(Task task) {
        TaskView taskView = task.getTaskView();
        if (taskView == null) {
            return null;
        }

        Long taskViewId = taskView.getId();
        if (taskViewId == null) {
            return taskView;
        }

        return taskViewService.findById(taskViewId).orElse(taskView);
    }

    public boolean hasRenderableTemplate(TaskView taskView) {
        return taskView != null
            && taskView.getTemplatePath() != null
            && !taskView.getTemplatePath().isBlank();
    }
}
