package com.example.studenttask.service;

import com.example.studenttask.model.TaskReview;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskStatusCode;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.repository.TaskReviewRepository;
import com.example.studenttask.repository.UserTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskReviewService {

    private static final Logger log = LoggerFactory.getLogger(TaskReviewService.class);

    private final TaskReviewRepository taskReviewRepository;
    private final UserTaskRepository userTaskRepository;
    private final TaskStatusService taskStatusService;
    private final SubmissionService submissionService;

    public TaskReviewService(TaskReviewRepository taskReviewRepository,
                           UserTaskRepository userTaskRepository,
                           TaskStatusService taskStatusService,
                           SubmissionService submissionService) {
        this.taskReviewRepository = taskReviewRepository;
        this.userTaskRepository = userTaskRepository;
        this.taskStatusService = taskStatusService;
        this.submissionService = submissionService;
    }

    /**
     * Find all reviews for a user task ordered by review date descending
     */
    public List<TaskReview> findByUserTaskOrderByReviewedAtDesc(UserTask userTask) {
        return taskReviewRepository.findByUserTaskOrderByReviewedAtDesc(userTask);
    }

    /**
     * Save a task review
     */
    public TaskReview save(TaskReview taskReview) {
        return taskReviewRepository.save(taskReview);
    }

    /**
     * Find review by id
     */
    public Optional<TaskReview> findById(Long id) {
        return taskReviewRepository.findById(id);
    }

    /**
     * Find all reviews by a specific reviewer
     */
    public List<TaskReview> findByReviewer(com.example.studenttask.model.User reviewer) {
        return taskReviewRepository.findByReviewerOrderByReviewedAtDesc(reviewer);
    }

    /**
     * Find the latest review for a user task
     */
    public Optional<TaskReview> findLatestReviewForUserTask(UserTask userTask) {
        return taskReviewRepository.findFirstByUserTaskOrderByReviewedAtDesc(userTask);
    }

    /**
     * Find all reviews for a user task
     */
    public List<TaskReview> findByUserTask(UserTask userTask) {
        return taskReviewRepository.findByUserTaskOrderByReviewedAtDesc(userTask);
    }

    /**
     * Create a new review for a user task with version and debug output
     */
    @Transactional
    public TaskReview createReview(UserTask userTask, User reviewer, Long statusId, String comment, Long submissionId, Integer currentVersion) {
        log.debug("Creating review for userTask {} by reviewer {} with status {} and version {}",
                userTask.getId(), reviewer.getId(), statusId, currentVersion);

        TaskReview review = new TaskReview();
        review.setUserTask(userTask);
        review.setReviewer(reviewer);

        TaskStatus status = taskStatusService.findById(statusId)
            .orElseThrow(() -> new RuntimeException("Status not found"));
        review.setStatus(status);
        log.debug("Resolved review status {}", status.getName());

        review.setComment(comment);
        review.setReviewedAt(LocalDateTime.now());

        if (currentVersion != null && currentVersion > 0) {
            review.setVersion(currentVersion);
            log.debug("Assigned review version {}", currentVersion);
        } else {
            log.debug("No review version provided");
        }

        userTask.setStatus(status);
        userTask.setLastModified(LocalDateTime.now());
        userTaskRepository.save(userTask);
        log.debug("Updated UserTask {} to status {}", userTask.getId(), status.getName());

        TaskReview savedReview = save(review);
        log.info("Created review {} for userTask {} with status {}",
                savedReview.getId(), userTask.getId(), status.getName());

        return savedReview;
    }

    /**
     * Get available status options for teacher reviews
     */
    public List<TaskStatus> getTeacherReviewStatuses() {
        List<TaskStatus> teacherStatuses = new ArrayList<>();

        taskStatusService.findByCode(TaskStatusCode.VOLLSTAENDIG).ifPresent(teacherStatuses::add);
        taskStatusService.findByCode(TaskStatusCode.UEBERARBEITUNG_NOETIG).ifPresent(teacherStatuses::add);

        return teacherStatuses;
    }

    public boolean hasReviewsForVersion(UserTask userTask, Integer version) {
        return taskReviewRepository.existsByUserTaskAndVersion(userTask, version);
    }

    public long countReviewsForVersion(UserTask userTask, Integer version) {
        return taskReviewRepository.countByUserTaskAndVersion(userTask, version);
    }
}
