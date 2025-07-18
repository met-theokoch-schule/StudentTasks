package com.example.studenttask.service;

import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.repository.TaskContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskContentService {

    @Autowired
    private TaskContentRepository taskContentRepository;

    /**
     * Save task content (creates new version)
     */
    public TaskContent saveContent(UserTask userTask, String content, boolean isSubmitted) {
        // Get the latest version number for this user task
        int nextVersion = getNextVersionNumber(userTask);

        TaskContent taskContent = new TaskContent();
        taskContent.setUserTask(userTask);
        taskContent.setContent(content);
        taskContent.setVersion(nextVersion);
        taskContent.setSavedAt(LocalDateTime.now());
        taskContent.setSubmitted(isSubmitted);

        return taskContentRepository.save(taskContent);
    }

    /**
     * Get the latest content for a user task
     */
    public Optional<TaskContent> getLatestContent(UserTask userTask) {
        return taskContentRepository.findTopByUserTaskOrderByVersionDesc(userTask);
    }

    /**
     * Get latest submitted content for a user task
     */
    public Optional<TaskContent> getLatestSubmittedContent(UserTask userTask) {
        return taskContentRepository.findTopByUserTaskAndIsSubmittedOrderByVersionDesc(userTask, true);
    }

    /**
     * Get all content versions for a user task
     */
    public List<TaskContent> getAllContentVersions(UserTask userTask) {
        return taskContentRepository.findByUserTaskOrderByVersionDesc(userTask);
    }

    /**
     * Get specific content version
     */
    public Optional<TaskContent> getContentVersion(UserTask userTask, int version) {
        return taskContentRepository.findByUserTaskAndVersion(userTask, version);
    }

    /**
     * Get all submitted versions for a user task
     */
    public List<TaskContent> getSubmittedVersions(UserTask userTask) {
        return taskContentRepository.findByUserTaskAndIsSubmittedOrderByVersionDesc(userTask, true);
    }

    /**
     * Mark latest content as submitted
     */
    public Optional<TaskContent> submitLatestContent(UserTask userTask) {
        Optional<TaskContent> latestOpt = getLatestContent(userTask);
        if (latestOpt.isPresent()) {
            TaskContent latest = latestOpt.get();
            if (!latest.isSubmitted()) {
                latest.setSubmitted(true);
                return Optional.of(taskContentRepository.save(latest));
            }
        }
        return latestOpt;
    }

    /**
     * Create a new version based on existing content
     */
    public TaskContent createNewVersionFromExisting(UserTask userTask, int sourceVersion, 
                                                   boolean isSubmitted) {
        Optional<TaskContent> sourceOpt = getContentVersion(userTask, sourceVersion);
        if (sourceOpt.isPresent()) {
            TaskContent source = sourceOpt.get();
            return saveContent(userTask, source.getContent(), isSubmitted);
        }
        throw new RuntimeException("Source version not found");
    }

    /**
     * Get next version number for a user task
     */
    private int getNextVersionNumber(UserTask userTask) {
        Optional<TaskContent> latestOpt = taskContentRepository.findTopByUserTaskOrderByVersionDesc(userTask);
        return latestOpt.map(content -> content.getVersion() + 1).orElse(1);
    }

    /**
     * Count total versions for a user task
     */
    public long countVersions(UserTask userTask) {
        return taskContentRepository.countByUserTask(userTask);
    }

    /**
     * Count submitted versions for a user task
     */
    public long countSubmittedVersions(UserTask userTask) {
        return taskContentRepository.countByUserTaskAndIsSubmitted(userTask, true);
    }

    /**
     * Delete all content for a user task
     */
    public void deleteAllContentForUserTask(UserTask userTask) {
        taskContentRepository.deleteByUserTask(userTask);
    }

    /**
     * Check if user task has any content
     */
    public boolean hasContent(UserTask userTask) {
        return taskContentRepository.existsByUserTask(userTask);
    }

    /**
     * Check if user task has submitted content
     */
    public boolean hasSubmittedContent(UserTask userTask) {
        return taskContentRepository.existsByUserTaskAndIsSubmitted(userTask, true);
    }

    /**
     * Get content statistics for a user task
     */
    public ContentStatistics getContentStatistics(UserTask userTask) {
        long totalVersions = countVersions(userTask);
        long submittedVersions = countSubmittedVersions(userTask);
        Optional<TaskContent> latestOpt = getLatestContent(userTask);

        return new ContentStatistics(
            totalVersions,
            submittedVersions,
            latestOpt.map(TaskContent::getSavedAt).orElse(null),
            latestOpt.map(TaskContent::isSubmitted).orElse(false)
        );
    }

    /**
     * Simple statistics class for content
     */
    public static class ContentStatistics {
        private final long totalVersions;
        private final long submittedVersions;
        private final LocalDateTime lastSaved;
        private final boolean latestIsSubmitted;

        public ContentStatistics(long totalVersions, long submittedVersions, 
                                LocalDateTime lastSaved, boolean latestIsSubmitted) {
            this.totalVersions = totalVersions;
            this.submittedVersions = submittedVersions;
            this.lastSaved = lastSaved;
            this.latestIsSubmitted = latestIsSubmitted;
        }

        public long getTotalVersions() { return totalVersions; }
        public long getSubmittedVersions() { return submittedVersions; }
        public LocalDateTime getLastSaved() { return lastSaved; }
        public boolean isLatestIsSubmitted() { return latestIsSubmitted; }
    }

    public TaskContent saveContent(UserTask userTask, String content) {
        TaskContent taskContent = new TaskContent();
        taskContent.setUserTask(userTask);
        taskContent.setContent(content);
        taskContent.setVersion(getNextVersionNumber(userTask));
        taskContent.setSavedAt(LocalDateTime.now());
        taskContent.setSubmitted(false);

        return taskContentRepository.save(taskContent);
    }

    public TaskContent submitContent(UserTask userTask, String content) {
        TaskContent taskContent = new TaskContent();
        taskContent.setUserTask(userTask);
        taskContent.setContent(content);
        taskContent.setVersion(getNextVersionNumber(userTask));
        taskContent.setSavedAt(LocalDateTime.now());
        taskContent.setSubmitted(true);

        return taskContentRepository.save(taskContent);
    }
}