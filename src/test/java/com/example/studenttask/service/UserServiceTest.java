package com.example.studenttask.service;

import com.example.studenttask.model.Role;
import com.example.studenttask.model.User;
import com.example.studenttask.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllTeachers_returnsUsersWithCanonicalAndLegacyTeacherRoleNames() {
        User canonicalTeacher = user("teacher-canonical", "ROLE_TEACHER");
        User legacyTeacher = user("teacher-legacy", "TEACHER");
        User germanTeacher = user("teacher-german", "Lehrer");
        User student = user("student", "ROLE_STUDENT");

        when(userRepository.findAll()).thenReturn(List.of(canonicalTeacher, legacyTeacher, germanTeacher, student));

        assertThat(userService.getAllTeachers())
            .extracting(User::getOpenIdSubject)
            .containsExactly("teacher-canonical", "teacher-legacy", "teacher-german");
    }

    @Test
    void getAllStudents_returnsUsersWithCanonicalAndLegacyStudentRoleNames() {
        User canonicalStudent = user("student-canonical", "ROLE_STUDENT");
        User legacyStudent = user("student-legacy", "STUDENT");
        User germanStudent = user("student-german", "Schueler");
        User teacher = user("teacher", "ROLE_TEACHER");

        when(userRepository.findAll()).thenReturn(List.of(canonicalStudent, legacyStudent, germanStudent, teacher));

        assertThat(userService.getAllStudents())
            .extracting(User::getOpenIdSubject)
            .containsExactly("student-canonical", "student-legacy", "student-german");
    }

    private User user(String openIdSubject, String roleName) {
        User user = new User(openIdSubject, openIdSubject, openIdSubject + "@example.invalid");
        user.setRoles(Set.of(new Role(roleName, roleName)));
        return user;
    }
}
