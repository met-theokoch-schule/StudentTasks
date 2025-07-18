
package com.example.studenttask.repository;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.User;
import com.example.studenttask.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByCreatedByAndIsActiveTrue(User createdBy);
    
    List<Task> findByIsActiveTrue();
    
    @Query("SELECT t FROM Task t JOIN t.assignedGroups g WHERE g IN :groups AND t.isActive = true")
    List<Task> findByAssignedGroupsInAndIsActiveTrue(@Param("groups") List<Group> groups);
    
    @Query("SELECT DISTINCT t FROM Task t JOIN t.assignedGroups g WHERE g = :group AND t.isActive = true")
    List<Task> findByAssignedGroup(@Param("group") Group group);
}
