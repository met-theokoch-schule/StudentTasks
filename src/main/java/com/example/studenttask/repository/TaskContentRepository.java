
package com.example.studenttask.repository;

import com.example.studenttask.model.TaskContent;
import com.example.studenttask.model.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskContentRepository extends JpaRepository<TaskContent, Long> {
    
    List<TaskContent> findByUserTaskOrderByVersionDesc(UserTask userTask);
    
    @Query("SELECT tc FROM TaskContent tc WHERE tc.userTask = :userTask ORDER BY tc.version DESC LIMIT 1")
    Optional<TaskContent> findLatestByUserTask(@Param("userTask") UserTask userTask);
    
    Optional<TaskContent> findByUserTaskAndVersion(UserTask userTask, Integer version);
    
    Optional<TaskContent> findTopByUserTaskOrderByVersionDesc(UserTask userTask);
    
    Optional<TaskContent> findTopByUserTaskAndIsSubmittedOrderByVersionDesc(UserTask userTask, Boolean isSubmitted);
    
    List<TaskContent> findByUserTaskAndIsSubmittedOrderByVersionDesc(UserTask userTask, Boolean isSubmitted);
    
    Long countByUserTask(UserTask userTask);
    
    Long countByUserTaskAndIsSubmitted(UserTask userTask, Boolean isSubmitted);
    
    void deleteByUserTask(UserTask userTask);
    
    boolean existsByUserTask(UserTask userTask);
    
    boolean existsByUserTaskAndIsSubmitted(UserTask userTask, Boolean isSubmitted);
}
