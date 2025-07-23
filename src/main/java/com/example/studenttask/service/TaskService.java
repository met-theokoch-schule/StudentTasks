package com.example.studenttask.service;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.User;
import com.example.studenttask.model.Group;
import com.example.studenttask.repository.TaskRepository;
import com.example.studenttask.repository.TaskViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskViewRepository taskViewRepository;

    /**
     * Create a new task
     */
    public Task createTask(String title, String description, String defaultSubmission, 
                          User createdBy, LocalDateTime dueDate, TaskView viewType, 
                          Set<Group> assignedGroups) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDefaultSubmission(defaultSubmission);
        task.setCreatedBy(createdBy);
        task.setCreatedAt(LocalDateTime.now());
        task.setDueDate(dueDate);
        task.setViewType(viewType);
        task.setAssignedGroups(assignedGroups);
        task.setIsActive(true);

        return taskRepository.save(task);
    }

    /**
     * Update an existing task
     */
    public Task updateTask(Long taskId, String title, String description, 
                          String defaultSubmission, LocalDateTime dueDate, 
                          TaskView viewType, Set<Group> assignedGroups) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setTitle(title);
            task.setDescription(description);
            task.setDefaultSubmission(defaultSubmission);
            task.setDueDate(dueDate);
            task.setViewType(viewType);
            task.setAssignedGroups(assignedGroups);

            return taskRepository.save(task);
        }
        throw new RuntimeException("Task not found with ID: " + taskId);
    }

    /**
     * Get all tasks created by a specific user
     */
    public List<Task> findTasksByCreator(User creator) {
        return taskRepository.findByCreatedByOrderByCreatedAtDesc(creator);
    }

    /**
     * Get all active tasks created by a specific user
     */
    public List<Task> findActiveTasksByCreator(User creator) {
        return taskRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(creator, true);
    }

    /**
     * Get all tasks assigned to a specific group
     */
    public List<Task> findTasksByGroup(Group group) {
        return taskRepository.findByAssignedGroupsContainingAndIsActiveOrderByCreatedAtDesc(group, true);
    }

    /**
     * Get all tasks assigned to any of the user's groups
     */
    public List<Task> findTasksForUser(User user) {
        return taskRepository.findTasksForUserGroups(user.getGroups());
    }

    /**
     * Get all overdue tasks
     */
    public List<Task> findOverdueTasks() {
        return taskRepository.findByDueDateBeforeAndIsActiveOrderByDueDateAsc(LocalDateTime.now(), true);
    }

    /**
     * Get tasks due soon (within specified hours)
     */
    public List<Task> findTasksDueSoon(int hours) {
        LocalDateTime cutoff = LocalDateTime.now().plusHours(hours);
        return taskRepository.findByDueDateBetweenAndIsActiveOrderByDueDateAsc(
            LocalDateTime.now(), cutoff, true);
    }

    /**
     * Find task by ID
     */
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    /**
     * Get all tasks
     */
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Get all active tasks
     */
    public List<Task> findAllActiveTasks() {
        return taskRepository.findByIsActiveOrderByCreatedAtDesc(true);
    }

    /**
     * Deactivate task
     */
    public void deactivateTask(Long taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setIsActive(false);
            taskRepository.save(task);
        }
    }

    /**
     * Activate task
     */
    public void activateTask(Long taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setIsActive(true);
            taskRepository.save(task);
        }
    }

    /**
     * Delete task
     */
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    /**
     * Check if user can access task (is creator or in assigned group)
     */
    public boolean canUserAccessTask(User user, Task task) {
        // Task creator can always access
        if (task.getCreatedBy().getId().equals(user.getId())) {
            return true;
        }

        // Check if user is in any of the assigned groups
        return task.getAssignedGroups().stream()
                .anyMatch(group -> user.getGroups().contains(group));
    }

    /**
     * Get task statistics for a creator
     */
    public TaskStatistics getTaskStatistics(User creator) {
        List<Task> allTasks = findTasksByCreator(creator);
        List<Task> activeTasks = findActiveTasksByCreator(creator);

        return new TaskStatistics(
            allTasks.size(),
            activeTasks.size(),
            allTasks.size() - activeTasks.size() // inactive tasks
        );
    }

    /**
     * Simple statistics class
     */
    public static class TaskStatistics {
        private final int totalTasks;
        private final int activeTasks;
        private final int inactiveTasks;

        public TaskStatistics(int totalTasks, int activeTasks, int inactiveTasks) {
            this.totalTasks = totalTasks;
            this.activeTasks = activeTasks;
            this.inactiveTasks = inactiveTasks;
        }

        public int getTotalTasks() { return totalTasks; }
        public int getActiveTasks() { return activeTasks; }
        public int getInactiveTasks() { return inactiveTasks; }
    }

    public List<Task> findByCreatedBy(User creator) {
        return taskRepository.findByCreatedByOrderByCreatedAtDesc(creator);
    }

    public Task createTask(Task task, List<Long> groupIds) {
        // Erst die Task speichern
        Task savedTask = taskRepository.save(task);

        // Dann die Gruppen zuweisen (wird später implementiert)
        // TODO: Implement group assignment

        return savedTask;
    }

    /**
     * Save a task
     */
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    /**
     * Find all active tasks
     */
    public List<Task> findActiveTasks() {
        return taskRepository.findByIsActiveTrue();
    }

    /**
     * Find all inactive tasks
     */
    public List<Task> findInactiveTasks() {
        return taskRepository.findByIsActiveFalse();
    }

    /**
     * Check if user has access to task
     */
    public boolean hasUserAccessToTask(User user, Task task) {
        return task.getAssignedGroups().stream()
            .anyMatch(group -> user.getGroups().contains(group));
    }

    /**
     * Delete a task
     */
    public void delete(Task task) {
        taskRepository.delete(task);
    }

    /**
     * Delete a task by id
     */
    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    /**
     * Find tasks by creator ordered by creation date descending
     */
    public List<Task> findByCreatedByOrderByCreatedAtDesc(User creator) {
        return taskRepository.findByCreatedByOrderByCreatedAtDesc(creator);
    }

    /**
     * Find active tasks by creator ordered by creation date descending
     */
    public List<Task> findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(User creator) {
        return taskRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(creator, true);
    }
}