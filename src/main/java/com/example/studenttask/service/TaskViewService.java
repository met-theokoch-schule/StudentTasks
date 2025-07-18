package com.example.studenttask.service;

import com.example.studenttask.model.TaskView;
import com.example.studenttask.repository.TaskViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskViewService {

    @Autowired
    private TaskViewRepository taskViewRepository;

    /**
     * Get all available task views
     */
    public List<TaskView> findAllTaskViews() {
        return taskViewRepository.findAll();
    }

    /**
     * Get all active task views
     */
    public List<TaskView> findActiveTaskViews() {
        return taskViewRepository.findByIsActiveOrderByName(true);
    }

    /**
     * Find task view by ID
     */
    public Optional<TaskView> findById(String id) {
        return taskViewRepository.findById(id);
    }

    /**
     * Find task view by name
     */
    public Optional<TaskView> findByName(String name) {
        return taskViewRepository.findByName(name);
    }

    /**
     * Create or update task view
     */
    public TaskView saveTaskView(TaskView taskView) {
        return taskViewRepository.save(taskView);
    }

    public List<TaskView> findAll() {
        return taskViewRepository.findAll();
    }

    /**
     * Create a new task view
     */
    public TaskView createTaskView(String id, String name, String description, 
                                  String templatePath, boolean isActive) {
        TaskView taskView = new TaskView();
        taskView.setId(id);
        taskView.setName(name);
        taskView.setDescription(description);
        taskView.setTemplatePath(templatePath);
        taskView.setActive(isActive);

        return taskViewRepository.save(taskView);
    }

    /**
     * Activate task view
     */
    public void activateTaskView(String id) {
        Optional<TaskView> taskViewOpt = taskViewRepository.findById(id);
        if (taskViewOpt.isPresent()) {
            TaskView taskView = taskViewOpt.get();
            taskView.setActive(true);
            taskViewRepository.save(taskView);
        }
    }

    /**
     * Deactivate task view
     */
    public void deactivateTaskView(String id) {
        Optional<TaskView> taskViewOpt = taskViewRepository.findById(id);
        if (taskViewOpt.isPresent()) {
            TaskView taskView = taskViewOpt.get();
            taskView.setActive(false);
            taskViewRepository.save(taskView);
        }
    }

    /**
     * Delete task view
     */
    public void deleteTaskView(String id) {
        taskViewRepository.deleteById(id);
    }

    /**
     * Check if task view exists and is active
     */
    public boolean isTaskViewActiveAndExists(String id) {
        Optional<TaskView> taskViewOpt = taskViewRepository.findById(id);
        return taskViewOpt.isPresent() && taskViewOpt.get().isActive();
    }

    /**
     * Get default task view (first active one)
     */
    public Optional<TaskView> getDefaultTaskView() {
        List<TaskView> activeViews = findActiveTaskViews();
        return activeViews.isEmpty() ? Optional.empty() : Optional.of(activeViews.get(0));
    }
}