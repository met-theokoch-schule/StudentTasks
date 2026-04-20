package com.example.studenttask.repository;

import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskContentRepository extends JpaRepository<TaskContent, Long> {

    List<TaskContent> findByUserTaskOrderByVersionDesc(UserTask userTask);
    
    TaskContent findByUserTaskAndVersion(UserTask userTask, Integer version);

    Optional<TaskContent> findTopByUserTaskOrderByVersionDesc(UserTask userTask);
    
    int countByUserTaskAndIsSubmittedTrue(UserTask userTask);
}
