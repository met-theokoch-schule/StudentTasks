
package com.example.studenttask.repository;

import com.example.studenttask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByOpenIdSubject(String openIdSubject);
    
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
    
    @Query("SELECT u FROM User u JOIN u.groups g WHERE g.name = :groupName")
    List<User> findByGroupName(@Param("groupName") String groupName);
    
    List<User> findByPreferredUsername(String preferredUsername);
}
package com.example.studenttask.repository;

import com.example.studenttask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByOpenIdSubject(String openIdSubject);
    Optional<User> findByEmail(String email);
    Optional<User> findByPreferredUsername(String preferredUsername);
}
