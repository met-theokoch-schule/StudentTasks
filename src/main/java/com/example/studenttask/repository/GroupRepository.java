package com.example.studenttask.repository;

import com.example.studenttask.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    /**
     * Find group by name
     */
    Optional<Group> findByName(String name);

    /**
     * Find all groups with users
     */
    @Query("SELECT DISTINCT g FROM Group g JOIN g.users")
    List<Group> findAllWithUsers();

    /**
     * Find all groups ordered by name
     */
    List<Group> findAllByOrderByName();

    /**
     * Check if group exists by name
     */
    boolean existsByName(String name);
}