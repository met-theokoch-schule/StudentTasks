package com.example.studenttask.repository;

import com.example.studenttask.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    /**
     * Alle aktiven Status sortiert nach order
     */
    List<TaskStatus> findByIsActiveTrueOrderByOrder();

    /**
     * Alle Status sortiert nach order
     */
    List<TaskStatus> findAllByOrderByOrder();

    /**
     * Status nach Name finden
     */
    Optional<TaskStatus> findByName(String name);

    /**
     * Status nach Namen-Set finden (nur aktive)
     */
    List<TaskStatus> findByNameInAndIsActiveTrue(Set<String> names);
}