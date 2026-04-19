package com.example.studenttask.controller;

import com.example.studenttask.dto.ApiOperationStatus;
import com.example.studenttask.dto.TaskContentCommandResultDto;
import com.example.studenttask.dto.TaskContentLoadResultDto;
import com.example.studenttask.dto.TaskContentRequestDto;
import com.example.studenttask.service.StudentTaskApiCommandService;
import com.example.studenttask.service.StudentTaskApiQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class StudentTaskApiController {

    private static final Logger log = LoggerFactory.getLogger(StudentTaskApiController.class);

    @Autowired
    private StudentTaskApiQueryService studentTaskApiQueryService;

    @Autowired
    private StudentTaskApiCommandService studentTaskApiCommandService;

    @GetMapping("/{taskId}/content")
    public ResponseEntity<String> getTaskContent(@PathVariable Long taskId, Authentication authentication) {
        try {
            TaskContentLoadResultDto result =
                studentTaskApiQueryService.getTaskContent(taskId, authentication.getName());

            if (result.getStatus() == ApiOperationStatus.UNAUTHORIZED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
            }
            if (result.getStatus() == ApiOperationStatus.NOT_FOUND) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result.getContent());
        } catch (Exception e) {
            log.error("Error loading task content for task {}", taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN') or @userService.hasTeacherRole(authentication.name)")
    @PostMapping("/usertasks/{userTaskId}/content")
    public ResponseEntity<String> saveUserTaskContent(@PathVariable Long userTaskId,
            @RequestBody TaskContentRequestDto request,
            Authentication authentication) {
        try {
            TaskContentCommandResultDto result = studentTaskApiCommandService.saveTeacherTaskContent(
                userTaskId,
                request.getContent(),
                authentication.getName()
            );
            if (result.getStatus() == ApiOperationStatus.NOT_FOUND) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok("Content saved successfully");
        } catch (Exception e) {
            log.error("Error saving teacher content for userTask {}", userTaskId, e);
            return ResponseEntity.status(500).body("Error saving content");
        }
    }

    @PostMapping("/{taskId}/content")
    public ResponseEntity<String> saveTaskContent(@PathVariable Long taskId,
            @RequestBody TaskContentRequestDto request,
            Authentication authentication) {
        try {
            TaskContentCommandResultDto result = studentTaskApiCommandService.saveTaskContent(
                taskId,
                authentication.getName(),
                request.getContent()
            );
            if (result.getStatus() == ApiOperationStatus.UNAUTHORIZED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found: " + authentication.getName());
            }
            if (result.getStatus() == ApiOperationStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found: " + taskId);
            }

            return ResponseEntity.ok("Content saved successfully (ID: " + result.getTaskContent().getId() + ", Version: "
                    + result.getTaskContent().getVersion() + ")");
        } catch (Exception e) {
            log.error("Error saving task content for task {}", taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving content: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    @PostMapping("/{taskId}/submit")
    public ResponseEntity<Void> submitTask(@PathVariable Long taskId,
            @RequestBody(required = false) TaskContentRequestDto request,
            Authentication authentication) {
        try {
            TaskContentCommandResultDto result = studentTaskApiCommandService.submitTask(
                taskId,
                authentication.getName(),
                request != null ? request.getContent() : null
            );
            if (result.getStatus() == ApiOperationStatus.UNAUTHORIZED) {
                return ResponseEntity.status(401).build();
            }
            if (result.getStatus() == ApiOperationStatus.NOT_FOUND) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error submitting task {}", taskId, e);
            return ResponseEntity.status(500).build();
        }
    }
}
