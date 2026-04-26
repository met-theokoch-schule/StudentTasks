package com.example.studenttask.service;

import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskStatusCode;
import com.example.studenttask.exception.TaskStatusNotFoundException;
import com.example.studenttask.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskStatusService {

    private static final Map<TaskStatusCode, Set<TaskStatusCode>> ALLOWED_TRANSITIONS = Map.of(
            TaskStatusCode.NICHT_BEGONNEN, Set.of(
                    TaskStatusCode.IN_BEARBEITUNG,
                    TaskStatusCode.ABGEGEBEN,
                    TaskStatusCode.VOLLSTAENDIG
            ),
            TaskStatusCode.IN_BEARBEITUNG, Set.of(
                    TaskStatusCode.NICHT_BEGONNEN,
                    TaskStatusCode.ABGEGEBEN,
                    TaskStatusCode.VOLLSTAENDIG
            ),
            TaskStatusCode.ABGEGEBEN, Set.of(
                    TaskStatusCode.IN_BEARBEITUNG,
                    TaskStatusCode.UEBERARBEITUNG_NOETIG,
                    TaskStatusCode.VOLLSTAENDIG
            ),
            TaskStatusCode.UEBERARBEITUNG_NOETIG, Set.of(
                    TaskStatusCode.IN_BEARBEITUNG,
                    TaskStatusCode.ABGEGEBEN,
                    TaskStatusCode.VOLLSTAENDIG
            ),
            TaskStatusCode.VOLLSTAENDIG, Set.of(
                    TaskStatusCode.ABGEGEBEN,
                    TaskStatusCode.UEBERARBEITUNG_NOETIG
            )
    );

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    /**
     * Alle aktiven Status abrufen, sortiert nach order
     */
    public List<TaskStatus> getAllActiveStatuses() {
        return taskStatusRepository.findByIsActiveTrueOrderByOrder();
    }

    /**
     * Status nach Name finden
     */
    public Optional<TaskStatus> findByName(String name) {
        return TaskStatusCode.fromName(name)
                .flatMap(this::findByCode)
                .or(() -> taskStatusRepository.findByName(name));
    }

    public Optional<TaskStatus> findByCode(TaskStatusCode code) {
        return taskStatusRepository.findByName(code.getDatabaseName());
    }

    public TaskStatus requireStatus(TaskStatusCode code) {
        return findByCode(code)
                .orElseThrow(() -> new TaskStatusNotFoundException(
                    "Status " + code.getDatabaseName() + " not found"
                ));
    }

    /**
     * Standard-Status abrufen (NICHT_BEGONNEN)
     */
    public TaskStatus getDefaultStatus() {
        return requireStatus(TaskStatusCode.NICHT_BEGONNEN);
    }

    /**
     * Prüfen, ob Status-Übergang erlaubt ist
     */
    public boolean isTransitionAllowed(TaskStatus fromStatus, TaskStatus toStatus) {
        if (fromStatus == null || toStatus == null) {
            return false;
        }

        Optional<TaskStatusCode> fromCode = getCode(fromStatus);
        Optional<TaskStatusCode> toCode = getCode(toStatus);
        if (fromCode.isEmpty() || toCode.isEmpty()) {
            return false;
        }

        return getAllowedTransitions(fromCode.get()).contains(toCode.get());
    }

    public Optional<TaskStatusCode> getCode(TaskStatus status) {
        return TaskStatusSupport.getCode(status);
    }

    public boolean isStatus(TaskStatus status, TaskStatusCode expectedCode) {
        return TaskStatusSupport.hasCode(status, expectedCode);
    }

    public Set<TaskStatusCode> getAllowedTransitions(TaskStatusCode fromStatusCode) {
        return ALLOWED_TRANSITIONS.getOrDefault(fromStatusCode, Set.of());
    }

    /**
     * Nächste mögliche Status für einen gegebenen Status
     */
    public List<TaskStatus> getNextPossibleStatuses(TaskStatus currentStatus) {
        if (currentStatus == null) {
            return List.of(getDefaultStatus());
        }

        Set<String> allowedNames = getCode(currentStatus)
                .map(this::getAllowedTransitions)
                .orElse(Set.of())
                .stream()
                .map(TaskStatusCode::getDatabaseName)
                .collect(Collectors.toSet());
        return taskStatusRepository.findByNameInAndIsActiveTrue(allowedNames);
    }

    /**
     * Status-Übergang durchführen mit Validierung
     */
    public boolean canTransitionTo(TaskStatus fromStatus, TaskStatus toStatus) {
        return isTransitionAllowed(fromStatus, toStatus);
    }

    /**
     * Alle Status für Admin-Interface
     */
    public List<TaskStatus> getAllStatuses() {
        return taskStatusRepository.findAllByOrderByOrder();
    }

    public List<TaskStatus> findAllActive() {
        return taskStatusRepository.findByIsActiveTrueOrderByOrderAsc();
    }

    public List<TaskStatus> findAll() {
        return taskStatusRepository.findAll();
    }

    public Optional<TaskStatus> findById(Long id) {
        return taskStatusRepository.findById(id);
    }

    public TaskStatus save(TaskStatus taskStatus) {
        return taskStatusRepository.save(taskStatus);
    }
}
