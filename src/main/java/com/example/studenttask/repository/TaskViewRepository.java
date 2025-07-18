
package com.example.studenttask.repository;

import com.example.studenttask.model.TaskView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskViewRepository extends JpaRepository<TaskView, String> {
    
    List<TaskView> findByIsActiveTrue();
}
