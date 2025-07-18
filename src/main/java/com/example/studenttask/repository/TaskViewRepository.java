package com.example.studenttask.repository;

import com.example.studenttask.model.TaskView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskViewRepository extends JpaRepository<TaskView, String> {

    List<TaskView> findAllByOrderByName();

    List<TaskView> findByIsActiveOrderByName(Boolean isActive);

    Optional<TaskView> findByName(String name);
}