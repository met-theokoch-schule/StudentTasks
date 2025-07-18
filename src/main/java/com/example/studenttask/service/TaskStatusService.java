
package com.example.studenttask.service;

import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TaskStatusService {

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
        return taskStatusRepository.findByName(name);
    }

    /**
     * Standard-Status abrufen (NICHT_BEGONNEN)
     */
    public TaskStatus getDefaultStatus() {
        return findByName("NICHT_BEGONNEN")
                .orElseThrow(() -> new RuntimeException("Default status NICHT_BEGONNEN not found"));
    }

    /**
     * Prüfen, ob Status-Übergang erlaubt ist
     */
    public boolean isTransitionAllowed(TaskStatus fromStatus, TaskStatus toStatus) {
        if (fromStatus == null || toStatus == null) {
            return false;
        }

        // Definiere erlaubte Übergänge basierend auf dem Status-Namen
        Set<String> allowedTransitions = getAllowedTransitions(fromStatus.getName());
        return allowedTransitions.contains(toStatus.getName());
    }

    /**
     * Definiert erlaubte Status-Übergänge
     */
    private Set<String> getAllowedTransitions(String fromStatusName) {
        return switch (fromStatusName) {
            case "NICHT_BEGONNEN" -> Set.of("IN_BEARBEITUNG");
            
            case "IN_BEARBEITUNG" -> Set.of("ABGEGEBEN", "NICHT_BEGONNEN");
            
            case "ABGEGEBEN" -> Set.of("IN_BEARBEITUNG", "ÜBERARBEITUNG_NÖTIG", "VOLLSTÄNDIG");
            
            case "ÜBERARBEITUNG_NÖTIG" -> Set.of("IN_BEARBEITUNG");
            
            case "VOLLSTÄNDIG" -> Set.of("ÜBERARBEITUNG_NÖTIG");
            
            default -> Set.of(); // Keine Übergänge erlaubt für unbekannte Status
        };
    }

    /**
     * Nächste mögliche Status für einen gegebenen Status
     */
    public List<TaskStatus> getNextPossibleStatuses(TaskStatus currentStatus) {
        if (currentStatus == null) {
            return List.of(getDefaultStatus());
        }

        Set<String> allowedNames = getAllowedTransitions(currentStatus.getName());
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

    /**
     * Neuen Status erstellen (für Admin)
     */
    public TaskStatus createStatus(String name, String description, Integer order) {
        TaskStatus status = new TaskStatus(name, description, order);
        return taskStatusRepository.save(status);
    }

    /**
     * Status deaktivieren (für Admin)
     */
    public void deactivateStatus(Long statusId) {
        taskStatusRepository.findById(statusId).ifPresent(status -> {
            status.setIsActive(false);
            taskStatusRepository.save(status);
        });
    }
}
