package com.example.studenttask.repository;

import com.example.studenttask.model.Task;
import com.example.studenttask.model.User;
import com.example.studenttask.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedGroupsContaining(Group group);

    @Query("SELECT t FROM Task t JOIN t.assignedGroups g WHERE g IN :groups")
    List<Task> findByAssignedGroupsIn(@Param("groups") Set<Group> groups);

    List<Task> findByCreatedByOrderByCreatedAtDesc(User createdBy);

    List<Task> findByCreatedByAndIsActiveOrderByCreatedAtDesc(User createdBy, Boolean isActive);

    List<Task> findByAssignedGroupsContainingAndIsActiveOrderByCreatedAtDesc(Group group, Boolean isActive);

    @Query("SELECT t FROM Task t JOIN t.assignedGroups g WHERE g IN :groups AND t.isActive = true ORDER BY t.createdAt DESC")
    List<Task> findTasksForUserGroups(@Param("groups") Set<Group> groups);

    List<Task> findByDueDateBeforeAndIsActiveOrderByDueDateAsc(LocalDateTime dueDate, Boolean isActive);

    List<Task> findByDueDateBetweenAndIsActiveOrderByDueDateAsc(LocalDateTime start, LocalDateTime end, Boolean isActive);

    List<Task> findByIsActiveOrderByCreatedAtDesc(Boolean isActive);
}