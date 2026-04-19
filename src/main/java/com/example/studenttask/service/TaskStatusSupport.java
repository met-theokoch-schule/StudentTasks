package com.example.studenttask.service;

import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskStatusCode;
import com.example.studenttask.model.UserTask;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class TaskStatusSupport {

    private TaskStatusSupport() {
    }

    public static Optional<TaskStatusCode> getCode(TaskStatus status) {
        if (status == null) {
            return Optional.empty();
        }
        return TaskStatusCode.fromName(status.getName());
    }

    public static boolean hasCode(TaskStatus status, TaskStatusCode expectedCode) {
        return getCode(status)
                .map(code -> code == expectedCode)
                .orElse(false);
    }

    public static Map<TaskStatusCode, Long> countByCode(Collection<UserTask> userTasks) {
        return userTasks.stream()
                .map(UserTask::getStatus)
                .map(TaskStatusSupport::getCode)
                .flatMap(Optional::stream)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    public static String iconClass(TaskStatus status) {
        TaskStatusCode code = getCode(status).orElse(null);
        if (code == null) {
            return "fas fa-circle text-secondary";
        }
        return switch (code) {
            case NICHT_BEGONNEN -> "fas fa-circle text-secondary";
            case IN_BEARBEITUNG -> "fas fa-edit text-primary";
            case ABGEGEBEN -> "fas fa-hourglass-half text-warning";
            case UEBERARBEITUNG_NOETIG -> "fas fa-redo text-danger";
            case VOLLSTAENDIG -> "fas fa-check-circle text-success";
        };
    }

    public static String textColorClass(TaskStatus status) {
        TaskStatusCode code = getCode(status).orElse(null);
        if (code == null) {
            return "text-secondary";
        }
        return switch (code) {
            case NICHT_BEGONNEN -> "text-secondary";
            case IN_BEARBEITUNG -> "text-primary";
            case ABGEGEBEN -> "text-warning";
            case UEBERARBEITUNG_NOETIG -> "text-danger";
            case VOLLSTAENDIG -> "text-success";
        };
    }

    public static String badgeClass(TaskStatus status) {
        TaskStatusCode code = getCode(status).orElse(null);
        if (code == null) {
            return "bg-secondary";
        }
        return switch (code) {
            case NICHT_BEGONNEN -> "bg-secondary";
            case IN_BEARBEITUNG -> "bg-warning";
            case ABGEGEBEN -> "bg-info";
            case UEBERARBEITUNG_NOETIG -> "bg-danger";
            case VOLLSTAENDIG -> "bg-success";
        };
    }
}
