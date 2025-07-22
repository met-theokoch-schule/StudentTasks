package com.example.studenttask.service;

import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.Submission;
import com.example.studenttask.dto.VersionWithSubmissionStatus;
import com.example.studenttask.repository.TaskContentRepository;
import com.example.studenttask.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskContentService {

    @Autowired
    private TaskContentRepository taskContentRepository;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private SubmissionService submissionService;

     @Autowired
    private UserTaskRepository userTaskRepository;


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

        TaskContent saved = taskContentRepository.save(taskContent);

        // Update UserTask timestamps
        userTask.setLastModified(LocalDateTime.now());
        if (userTask.getStartedAt() == null) {
            userTask.setStartedAt(LocalDateTime.now());
        }

        // Update status based on action
        if (isSubmitted) {
            // If submitted, set to ABGEGEBEN
            TaskStatus submittedStatus = taskStatusService.findByName("ABGEGEBEN")
                    .orElseThrow(() -> new RuntimeException("Status ABGEGEBEN not found"));
            userTask.setStatus(submittedStatus);

            // Create submission record
            submissionService.createSubmission(userTask, saved);
        } else {
            // If just saving (not submitting) and status is still NICHT_BEGONNEN, change to IN_BEARBEITUNG
            if (userTask.getStatus() != null && "NICHT_BEGONNEN".equals(userTask.getStatus().getName())) {
                TaskStatus inProgressStatus = taskStatusService.findByName("IN_BEARBEITUNG")
                        .orElseThrow(() -> new RuntimeException("Status IN_BEARBEITUNG not found"));
                userTask.setStatus(inProgressStatus);
            }
        }

        userTaskRepository.save(userTask);

        return saved;
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

    public TaskContent getContentByVersion(UserTask userTask, Integer version) {
        return taskContentRepository.findByUserTaskAndVersion(userTask, version);
    }

    /**
     * Get the next version number for a user task
     */
    private int getNextVersionNumber(UserTask userTask) {
        List<TaskContent> existingContents = taskContentRepository.findByUserTaskOrderByVersionDesc(userTask);
        return existingContents.isEmpty() ? 1 : existingContents.get(0).getVersion() + 1;
    }

    /**
     * Get the latest draft (non-submitted) content for a user task
     */
    public Optional<TaskContent> getLatestDraftContent(UserTask userTask) {
        List<TaskContent> contents = taskContentRepository.findByUserTaskAndIsSubmittedOrderByVersionDesc(userTask, false);
        return contents.isEmpty() ? Optional.empty() : Optional.of(contents.get(0));
    }

    /**
     * Check if user task has any submitted content
     */
    public boolean hasSubmittedContent(UserTask userTask) {
        return taskContentRepository.existsByUserTaskAndIsSubmittedTrue(userTask);
    }

    /**
     * Get count of submitted versions for a user task
     */
    public int getSubmittedVersionsCount(UserTask userTask) {
        return taskContentRepository.countByUserTaskAndIsSubmittedTrue(userTask);
    }

    /**
     * Get total count of all versions for a user task
     */
    public long getTotalVersionsCount(UserTask userTask) {
        return taskContentRepository.countByUserTask(userTask);
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
    public boolean hasAnyContent(UserTask userTask) {
        return taskContentRepository.existsByUserTask(userTask);
    }

    /**
     * Get versions with submission status for dropdown
     */
    public List<VersionWithSubmissionStatus> getVersionsWithSubmissionStatus(Long userTaskId) {
        Optional<UserTask> userTaskOpt = userTaskRepository.findById(userTaskId);
        if (userTaskOpt.isEmpty()) {
            return new ArrayList<>();
        }

        UserTask userTask = userTaskOpt.get();
        List<TaskContent> allVersions = taskContentRepository.findByUserTaskOrderByVersionDesc(userTask);
        List<VersionWithSubmissionStatus> versionsWithStatus = new ArrayList<>();

        for (TaskContent content : allVersions) {
            // Format the date and time
            String dateTime = "";
            if (content.getSavedAt() != null) {
                dateTime = content.getSavedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yy HH:mm"));
            }
            
            String displayText = "v" + content.getVersion() + " " + dateTime;
            
            if (content.isSubmitted()) {
                // Check if there are any reviews for this version
                boolean hasReviews = taskReviewService.hasReviewsForVersion(content.getUserTask(), content.getVersion());
                
                if (hasReviews) {
                    displayText += " 👁"; // Already reviewed
                } else {
                    displayText += " ⏳"; // Waiting for review
                }
            }

            versionsWithStatus.add(new VersionWithSubmissionStatus(
                content.getVersion(), 
                content.isSubmitted(), 
                displayText
            ));
        }

        return versionsWithStatus;
    }

    /**
     * Get all content versions ordered by saved date
     */
    public List<TaskContent> getAllContentVersionsByDate(UserTask userTask) {
        return taskContentRepository.findByUserTaskOrderBySavedAtDesc(userTask);
    }

    /**
     * Save draft content (not submitted)
     */
    public TaskContent saveDraft(UserTask userTask, String content) {
        return saveContent(userTask, content, false);
    }

    /**
     * Submit content for a user task (creates new version and marks as submitted)
     */
    public TaskContent submitContent(UserTask userTask, String content) {
        return saveContent(userTask, content, true);
    }

    /**
     * Mark existing content as submitted
     */
    public void markAsSubmitted(TaskContent taskContent) {
        taskContent.setSubmitted(true);
        taskContentRepository.save(taskContent);
    }
}