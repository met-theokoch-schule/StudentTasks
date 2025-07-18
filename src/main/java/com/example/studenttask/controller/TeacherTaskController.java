@GetMapping("/create")
    public String createTaskForm(Model model, Principal principal) {
        User teacher = userService.findByPreferredUsername(principal.getName());

        // Alle verfügbaren Task Views laden
        List<TaskView> taskViews = taskViewService.findAllActive();

        model.addAttribute("teacher", teacher);
        model.addAttribute("taskViews", taskViews);
        model.addAttribute("task", new Task());

        return "teacher/task-create";
    }

    /**
     * Zeigt alle Submissions für eine spezifische Aufgabe
     */
    @GetMapping("/{taskId}/submissions")
    public String viewTaskSubmissions(@PathVariable Long taskId, Model model, Principal principal) {
        User teacher = userService.findByPreferredUsername(principal.getName());

        // Task laden und Berechtigung prüfen
        Task task = taskService.findById(taskId);
        if (task == null || !task.getCreatedBy().equals(teacher)) {
            return "redirect:/teacher/tasks?error=not_found";
        }

        // Alle UserTasks für diese Aufgabe laden
        List<UserTask> userTasks = userTaskService.findByTask(task);

        // DTO für Template erstellen
        List<StudentSubmissionInfo> submissions = new ArrayList<>();
        for (UserTask userTask : userTasks) {
            StudentSubmissionInfo info = new StudentSubmissionInfo();
            info.setUserTask(userTask);
            info.setStudent(userTask.getUser());
            info.setStatus(userTask.getStatus());

            // Submission-Historie laden
            List<TaskContent> history = taskContentService.findByUserTaskOrderBySavedAtDesc(userTask);
            info.setSubmissionHistory(history);
            info.setHasSubmissions(!history.isEmpty());
            info.setSubmissionCount(history.size());

            // Letzte Aktivität
            if (!history.isEmpty()) {
                info.setLastActivity(history.get(0).getSavedAt());
            }

            submissions.add(info);
        }

        // Nach Gruppenzugehörigkeit sortieren (falls gewünscht)
        submissions.sort((a, b) -> a.getStudent().getName().compareTo(b.getStudent().getName()));

        model.addAttribute("teacher", teacher);
        model.addAttribute("task", task);
        model.addAttribute("submissions", submissions);

        return "teacher/task-submissions";
    }

    /**
     * DTO für die Submissions-Anzeige
     */
    public static class StudentSubmissionInfo {
        private UserTask userTask;
        private User student;
        private TaskStatus status;
        private List<TaskContent> submissionHistory;
        private boolean hasSubmissions;
        private int submissionCount;
        private LocalDateTime lastActivity;

        // Getters and Setters
        public UserTask getUserTask() { return userTask; }
        public void setUserTask(UserTask userTask) { this.userTask = userTask; }

        public User getStudent() { return student; }
        public void setStudent(User student) { this.student = student; }

        public TaskStatus getStatus() { return status; }
        public void setStatus(TaskStatus status) { this.status = status; }

        public List<TaskContent> getSubmissionHistory() { return submissionHistory; }
        public void setSubmissionHistory(List<TaskContent> submissionHistory) { this.submissionHistory = submissionHistory; }

        public boolean isHasSubmissions() { return hasSubmissions; }
        public void setHasSubmissions(boolean hasSubmissions) { this.hasSubmissions = hasSubmissions; }

        public int getSubmissionCount() { return submissionCount; }
        public void setSubmissionCount(int submissionCount) { this.submissionCount = submissionCount; }

        public LocalDateTime getLastActivity() { return lastActivity; }
        public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
    }