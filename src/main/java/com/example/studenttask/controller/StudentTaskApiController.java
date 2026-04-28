package com.example.studenttask.controller;

import com.example.studenttask.dto.StudentTaskListStateRequestDto;
import com.example.studenttask.dto.TaskContentRequestDto;
import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.User;
import com.example.studenttask.service.StudentTaskApiCommandService;
import com.example.studenttask.service.StudentTaskApiQueryService;
import com.example.studenttask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashSet;

@RestController
@RequestMapping("/api/tasks")
public class StudentTaskApiController {

    @Autowired
    private StudentTaskApiQueryService studentTaskApiQueryService;

    @Autowired
    private StudentTaskApiCommandService studentTaskApiCommandService;

    @Autowired
    private UserService userService;

    @GetMapping("/{taskId}/content")
    public ResponseEntity<String> getTaskContent(@PathVariable Long taskId, Authentication authentication) {
        return ResponseEntity.ok(
            studentTaskApiQueryService.getTaskContent(taskId, authentication.getName())
        );
    }

    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN') or @userService.hasTeacherRole(authentication.name)")
    @PostMapping("/usertasks/{userTaskId}/content")
    public ResponseEntity<String> saveUserTaskContent(@PathVariable Long userTaskId,
            @RequestBody TaskContentRequestDto request,
            Authentication authentication) {
        studentTaskApiCommandService.saveTeacherTaskContent(
            userTaskId,
            request.getContent(),
            authentication.getName()
        );
        return ResponseEntity.ok("Content saved successfully");
    }

    @PostMapping("/{taskId}/content")
    public ResponseEntity<String> saveTaskContent(@PathVariable Long taskId,
            @RequestBody TaskContentRequestDto request,
            Authentication authentication) {
        TaskContent savedContent = studentTaskApiCommandService.saveTaskContent(
            taskId,
            authentication.getName(),
            request.getContent()
        );

        return ResponseEntity.ok("Content saved successfully (ID: " + savedContent.getId() + ", Version: "
                + savedContent.getVersion() + ")");
    }

    @PostMapping("/{taskId}/submit")
    public ResponseEntity<Void> submitTask(@PathVariable Long taskId,
            @RequestBody(required = false) TaskContentRequestDto request,
            Authentication authentication) {
        studentTaskApiCommandService.submitTask(
            taskId,
            authentication.getName(),
            request != null ? request.getContent() : null
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/list-state")
    public ResponseEntity<Void> saveTaskListState(@RequestBody StudentTaskListStateRequestDto request,
            Authentication authentication) {
        User student = userService.findByOpenIdSubject(authentication.getName())
            .orElseThrow(() -> new IllegalStateException("Benutzer nicht gefunden"));
        userService.saveStudentTaskListExpandedUnits(
            student,
            new LinkedHashSet<>(request.getExpandedUnitIds())
        );
        return ResponseEntity.ok().build();
    }
}
