
package com.example.studenttask.repository;

import com.example.studenttask.model.UserTask;
import com.example.studenttask.model.User;
import com.example.studenttask.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {
    
    Optional<UserTask> findByUserAndTask(User user, Task task);
    
    List<UserTask> findByUser(User user);
    
    List<UserTask> findByTask(Task task);
    
    @Query("SELECT ut FROM UserTask ut WHERE ut.task = :task ORDER BY ut.user.name")
    List<UserTask> findByTaskOrderByUserName(@Param("task") Task task);
}
