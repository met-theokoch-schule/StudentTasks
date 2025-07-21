@PostMapping("/api/tasks/{taskId}/submit")
    public ResponseEntity<String> submitTask(@PathVariable Long taskId, 
                                           @RequestBody Map<String, String> request,
                                           Authentication authentication) {
        try {
            String content = request.get("content");
            User user = userService.findByOpenIdSubject(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            Optional<Task> taskOpt = taskService.findById(taskId);
            if (taskOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            boolean success = submissionService.submitTask(user, taskOpt.get(), content);
            if (success) {
                return ResponseEntity.ok("Task submitted successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to submit task");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/api/usertasks/{userTaskId}/content")
    public ResponseEntity<String> saveContentForUserTask(@PathVariable Long userTaskId,
                                                        @RequestBody Map<String, String> request,
                                                        Authentication authentication) {
        try {
            String content = request.get("content");

            // Verify teacher permissions
            User teacher = userService.findByOpenIdSubject(authentication.getName()).orElse(null);
            if (teacher == null || !teacher.hasRole("TEACHER")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            Optional<UserTask> userTaskOpt = userTaskService.findById(userTaskId);
            if (userTaskOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            UserTask userTask = userTaskOpt.get();

            // Save content with teacher as modifier (but keep original user)
            TaskContent savedContent = taskContentService.saveContent(userTask, content, false);

            return ResponseEntity.ok("Content saved successfully. Version: " + savedContent.getVersion());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }