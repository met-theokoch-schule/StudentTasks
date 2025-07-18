
package com.example.studenttask.repository;

import com.example.studenttask.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {
    
    Optional<TaskStatus> findByName(String name);
    
    List<TaskStatus> findByIsActiveTrueOrderByOrder();
}
