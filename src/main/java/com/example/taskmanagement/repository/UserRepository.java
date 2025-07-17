
package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByOpenIdSubject(String openIdSubject);
    Optional<User> findByEmail(String email);
}
package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySubject(String subject);
    Optional<User> findByEmail(String email);
    Optional<User> findByPreferredUsername(String preferredUsername);
}
