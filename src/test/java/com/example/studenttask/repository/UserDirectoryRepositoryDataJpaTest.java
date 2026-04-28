package com.example.studenttask.repository;

import com.example.studenttask.model.Group;
import com.example.studenttask.model.Role;
import com.example.studenttask.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:sqlite:file:user-directory-repository-test?mode=memory&cache=shared",
    "spring.datasource.driver-class-name=org.sqlite.JDBC",
    "spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserDirectoryRepositoryDataJpaTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void groupRepository_returnsDistinctGroupsWithUsersAndSupportsMembershipLookups() {
        Group alpha = groupRepository.saveAndFlush(new Group("Alpha", "Alpha Gruppe"));
        Group beta = groupRepository.saveAndFlush(new Group("Beta", "Beta Gruppe"));
        Group empty = groupRepository.saveAndFlush(new Group("Leer", "Ohne Benutzer"));

        User firstUser = user("user-alpha-beta", "User Alpha Beta");
        firstUser.addGroup(alpha);
        firstUser.addGroup(beta);

        User secondUser = user("user-alpha", "User Alpha");
        secondUser.addGroup(alpha);

        userRepository.saveAndFlush(firstUser);
        userRepository.saveAndFlush(secondUser);

        assertThat(groupRepository.findAllWithUsers())
            .extracting(Group::getName)
            .containsExactlyInAnyOrder("Alpha", "Beta");

        assertThat(groupRepository.findByUsersContaining(firstUser))
            .extracting(Group::getName)
            .containsExactlyInAnyOrder("Alpha", "Beta");

        assertThat(groupRepository.findAllByOrderByName())
            .extracting(Group::getName)
            .containsExactly("Alpha", "Beta", "Leer");

        assertThat(groupRepository.existsByName("Leer")).isTrue();
        assertThat(groupRepository.findByName("Leer")).contains(empty);
    }

    @Test
    void userRepository_filtersUsersByExactStoredRoleAndGroupMembership() {
        Role roleTeacher = roleRepository.saveAndFlush(new Role("ROLE_TEACHER", "Teacher"));
        Role legacyTeacher = roleRepository.saveAndFlush(new Role("TEACHER", "Legacy teacher"));
        Role roleStudent = roleRepository.saveAndFlush(new Role("ROLE_STUDENT", "Student"));
        Group q2 = groupRepository.saveAndFlush(new Group("Q2", "Q2 Gruppe"));
        Group q3 = groupRepository.saveAndFlush(new Group("Q3", "Q3 Gruppe"));

        User canonicalTeacher = user("teacher-role", "Canonical Teacher");
        canonicalTeacher.setPreferredUsername("canonical-teacher");
        canonicalTeacher.addRole(roleTeacher);
        canonicalTeacher.addGroup(q2);

        User legacyTeacherUser = user("teacher-legacy", "Legacy Teacher");
        legacyTeacherUser.addRole(legacyTeacher);
        legacyTeacherUser.addGroup(q2);

        User student = user("student-role", "Student");
        student.addRole(roleStudent);
        student.addGroup(q3);

        userRepository.saveAndFlush(canonicalTeacher);
        userRepository.saveAndFlush(legacyTeacherUser);
        userRepository.saveAndFlush(student);

        assertThat(userRepository.findByRoleName("ROLE_TEACHER"))
            .extracting(User::getOpenIdSubject)
            .containsExactly("teacher-role");

        assertThat(userRepository.findByRoleName("TEACHER"))
            .extracting(User::getOpenIdSubject)
            .containsExactly("teacher-legacy");

        assertThat(userRepository.findByGroupName("Q2"))
            .extracting(User::getOpenIdSubject)
            .containsExactlyInAnyOrder("teacher-role", "teacher-legacy");

        assertThat(userRepository.countByGroupsContaining(q2)).isEqualTo(2);
        assertThat(userRepository.findByPreferredUsername("canonical-teacher")).isEqualTo(canonicalTeacher);
    }

    private User user(String openIdSubject, String name) {
        return new User(openIdSubject, name, openIdSubject + "@example.invalid");
    }
}
