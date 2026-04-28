package com.example.studenttask.service;

import com.example.studenttask.dto.GroupOverviewDto;
import com.example.studenttask.dto.GroupStatisticsDto;
import com.example.studenttask.dto.StudentTaskMatrixDto;
import com.example.studenttask.dto.StudentTaskStatusDto;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Role;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.repository.TaskContentRepository;
import com.example.studenttask.repository.TaskRepository;
import com.example.studenttask.repository.UserRepository;
import com.example.studenttask.repository.UserTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupQueryServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserTaskRepository userTaskRepository;

    @Mock
    private TaskContentRepository taskContentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private GroupQueryService groupQueryService;

    @Test
    void getGroupsWithActiveTasksByTeacher_countsOnlyTeacherOwnedStudentSubmissionsAndLastActivity() {
        Group activeGroup = group(10L, "10A");
        Group emptyGroup = group(11L, "11B");
        User teacher = teacher(1L, "Teacher", activeGroup, emptyGroup);
        User otherTeacher = teacher(4L, "Other Teacher", activeGroup);
        User studentStarted = student(2L, "Ada", activeGroup, "Lovelace");
        User studentMissing = student(3L, "Grace", activeGroup, "Hopper");

        Task teacherTask = task(20L, "Queries", teacher, activeGroup, unitTitle("sql", "SQL", 10));
        UserTask startedTask = userTask(30L, studentStarted, teacherTask, status("ABGEGEBEN"),
            LocalDateTime.of(2026, 4, 19, 10, 30));

        when(taskRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(teacher, true))
            .thenReturn(List.of(teacherTask));
        when(userRepository.findByGroupsContaining(activeGroup))
            .thenReturn(List.of(teacher, otherTeacher, studentStarted, studentMissing));
        when(userRepository.findByGroupsContaining(emptyGroup)).thenReturn(List.of());
        when(userService.hasStudentRole(teacher)).thenReturn(false);
        when(userService.hasStudentRole(otherTeacher)).thenReturn(false);
        when(userService.hasStudentRole(studentStarted)).thenReturn(true);
        when(userService.hasStudentRole(studentMissing)).thenReturn(true);
        when(userTaskRepository.findByUserAndTask(studentStarted, teacherTask)).thenReturn(Optional.of(startedTask));
        when(userTaskRepository.findByUserAndTask(studentMissing, teacherTask)).thenReturn(Optional.empty());

        List<GroupOverviewDto> result = groupQueryService.getGroupsWithActiveTasksByTeacher(teacher);

        assertThat(result).hasSize(1);
        GroupOverviewDto overview = result.get(0);
        assertThat(overview.getGroup()).isSameAs(activeGroup);
        assertThat(overview.getStudentCount()).isEqualTo(2);
        assertThat(overview.getActiveTaskCount()).isEqualTo(1);
        assertThat(overview.getPendingSubmissions()).isEqualTo(1);
        assertThat(overview.getLastActivity()).isEqualTo(LocalDateTime.of(2026, 4, 19, 10, 30));
    }

    @Test
    void getGroupsWithActiveTasksByTeacher_includesTeachersWhenConfiguredForLocalViews() {
        Group activeGroup = group(10L, "10A");
        User teacher = teacher(1L, "Zulu Teacher", activeGroup);
        User student = student(2L, "Ada", activeGroup, "Lovelace");

        Task teacherTask = task(20L, "Queries", teacher, activeGroup, unitTitle("sql", "SQL", 10));
        UserTask submittedTeacherTask = userTask(30L, teacher, teacherTask, status("ABGEGEBEN"),
            LocalDateTime.of(2026, 4, 20, 9, 15));

        ReflectionTestUtils.setField(groupQueryService, "includeTeachersInTeacherViews", true);

        when(taskRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(teacher, true))
            .thenReturn(List.of(teacherTask));
        when(userRepository.findByGroupsContaining(activeGroup)).thenReturn(List.of(teacher, student));
        when(userService.hasStudentRole(teacher)).thenReturn(false);
        when(userService.hasTeacherRole(teacher)).thenReturn(true);
        when(userService.hasStudentRole(student)).thenReturn(true);
        when(userTaskRepository.findByUserAndTask(teacher, teacherTask)).thenReturn(Optional.of(submittedTeacherTask));
        when(userTaskRepository.findByUserAndTask(student, teacherTask)).thenReturn(Optional.empty());

        List<GroupOverviewDto> result = groupQueryService.getGroupsWithActiveTasksByTeacher(teacher);

        assertThat(result).hasSize(1);
        GroupOverviewDto overview = result.get(0);
        assertThat(overview.getStudentCount()).isEqualTo(2);
        assertThat(overview.getPendingSubmissions()).isEqualTo(1);
        assertThat(overview.getLastActivity()).isEqualTo(LocalDateTime.of(2026, 4, 20, 9, 15));
    }

    @Test
    void getStudentTaskMatrix_sortsOnlyStudentsAndTeacherOwnedTasksAndBuildsTypedStatuses() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        User otherTeacher = teacher(4L, "Other Teacher", group);
        User secondStudent = student(2L, "Berta", group, "Zimmer");
        User firstStudent = student(3L, "Anna", group, "Alpha");

        Task laterTask = task(20L, "B Task", teacher, group, unitTitle("advanced", "Advanced", 20));
        Task earlierTask = task(21L, "A Task", teacher, group, unitTitle("basics", "Basics", 10));
        Task foreignTask = task(22L, "Foreign Task", otherTeacher, group, unitTitle("foreign", "Foreign", 30));
        UserTask submittedTask = userTask(40L, firstStudent, earlierTask, status("ABGEGEBEN"), null);

        when(userRepository.findByGroupsContaining(group)).thenReturn(List.of(teacher, secondStudent, firstStudent));
        when(userService.hasStudentRole(teacher)).thenReturn(false);
        when(userService.hasStudentRole(secondStudent)).thenReturn(true);
        when(userService.hasStudentRole(firstStudent)).thenReturn(true);
        when(taskRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(teacher, true))
            .thenReturn(List.of(laterTask, earlierTask));
        when(userTaskRepository.findByUserAndTask(firstStudent, earlierTask)).thenReturn(Optional.of(submittedTask));
        when(userTaskRepository.findByUserAndTask(firstStudent, laterTask)).thenReturn(Optional.empty());
        when(userTaskRepository.findByUserAndTask(secondStudent, earlierTask)).thenReturn(Optional.empty());
        when(userTaskRepository.findByUserAndTask(secondStudent, laterTask)).thenReturn(Optional.empty());
        when(taskContentRepository.countByUserTaskAndIsSubmittedTrue(submittedTask)).thenReturn(2);

        StudentTaskMatrixDto matrix = groupQueryService.getStudentTaskMatrix(group, teacher);

        assertThat(matrix.getStudents()).containsExactly(firstStudent, secondStudent);
        assertThat(matrix.getTasks()).containsExactly(earlierTask, laterTask);
        assertThat(matrix.getTasks()).doesNotContain(foreignTask);

        StudentTaskStatusDto submittedStatus = matrix.getStatus(firstStudent.getId(), earlierTask.getId());
        assertThat(submittedStatus).isNotNull();
        assertThat(submittedStatus.getUserTaskId()).isEqualTo(40L);
        assertThat(submittedStatus.isHasSubmissions()).isTrue();
        assertThat(submittedStatus.getStatusIcon()).isEqualTo("fas fa-hourglass-half text-warning");

        StudentTaskStatusDto emptyStatus = matrix.getStatus(secondStudent.getId(), laterTask.getId());
        assertThat(emptyStatus).isNotNull();
        assertThat(emptyStatus.getUserTaskId()).isNull();
        assertThat(emptyStatus.isHasSubmissions()).isFalse();
        assertThat(emptyStatus.getStatus()).isNull();
    }

    @Test
    void getStudentTaskMatrix_includesTeachersWhenConfiguredForLocalViews() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Zulu Teacher", group);
        User student = student(2L, "Anna", group, "Alpha");

        Task task = task(20L, "A Task", teacher, group, unitTitle("basics", "Basics", 10));

        ReflectionTestUtils.setField(groupQueryService, "includeTeachersInTeacherViews", true);

        when(userRepository.findByGroupsContaining(group)).thenReturn(List.of(teacher, student));
        when(userService.hasStudentRole(teacher)).thenReturn(false);
        when(userService.hasTeacherRole(teacher)).thenReturn(true);
        when(userService.hasStudentRole(student)).thenReturn(true);
        when(taskRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(teacher, true)).thenReturn(List.of(task));
        when(userTaskRepository.findByUserAndTask(student, task)).thenReturn(Optional.empty());
        when(userTaskRepository.findByUserAndTask(teacher, task)).thenReturn(Optional.empty());

        StudentTaskMatrixDto matrix = groupQueryService.getStudentTaskMatrix(group, teacher);

        assertThat(matrix.getStudents()).containsExactly(student, teacher);
    }

    @Test
    void getGroupStatistics_countsOnlyTeacherOwnedStatusesForGroup() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        User otherTeacher = teacher(4L, "Other Teacher", group);
        User studentA = student(2L, "Ada", group, "Lovelace");
        User studentB = student(3L, "Grace", group, "Hopper");

        Task teacherTask = task(20L, "Teacher Task", teacher, group, null);
        Task completedTask = task(21L, "Completed Task", teacher, group, null);

        UserTask submitted = userTask(30L, studentA, teacherTask, status("ABGEGEBEN"), null);
        UserTask revision = userTask(31L, studentB, teacherTask, status("UEBERARBEITUNG_NOETIG"), null);
        UserTask complete = userTask(32L, studentA, completedTask, status("VOLLSTAENDIG"), null);

        when(taskRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(teacher, true))
            .thenReturn(List.of(teacherTask, completedTask));
        when(userRepository.findByGroupsContaining(group)).thenReturn(List.of(teacher, studentA, studentB));
        when(userService.hasStudentRole(teacher)).thenReturn(false);
        when(userService.hasStudentRole(studentA)).thenReturn(true);
        when(userService.hasStudentRole(studentB)).thenReturn(true);
        when(userTaskRepository.findByUserAndTask(studentA, teacherTask)).thenReturn(Optional.of(submitted));
        when(userTaskRepository.findByUserAndTask(studentB, teacherTask)).thenReturn(Optional.of(revision));
        when(userTaskRepository.findByUserAndTask(studentA, completedTask)).thenReturn(Optional.of(complete));
        when(userTaskRepository.findByUserAndTask(studentB, completedTask)).thenReturn(Optional.empty());

        GroupStatisticsDto statistics = groupQueryService.getGroupStatistics(group, teacher);

        assertThat(statistics.getTotalStudents()).isEqualTo(2);
        assertThat(statistics.getSubmittedTasks()).isEqualTo(1);
        assertThat(statistics.getNeedsRevisionTasks()).isEqualTo(1);
        assertThat(statistics.getCompletedTasks()).isEqualTo(1);
    }

    @Test
    void getGroupStatistics_includesTeachersWhenConfiguredForLocalViews() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        User student = student(2L, "Ada", group, "Lovelace");

        Task teacherTask = task(20L, "Teacher Task", teacher, group, null);
        UserTask teacherSubmission = userTask(30L, teacher, teacherTask, status("ABGEGEBEN"), null);
        UserTask studentSubmission = userTask(31L, student, teacherTask, status("ABGEGEBEN"), null);

        ReflectionTestUtils.setField(groupQueryService, "includeTeachersInTeacherViews", true);

        when(taskRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(teacher, true))
            .thenReturn(List.of(teacherTask));
        when(userRepository.findByGroupsContaining(group)).thenReturn(List.of(teacher, student));
        when(userService.hasStudentRole(teacher)).thenReturn(false);
        when(userService.hasTeacherRole(teacher)).thenReturn(true);
        when(userService.hasStudentRole(student)).thenReturn(true);
        when(userTaskRepository.findByUserAndTask(teacher, teacherTask)).thenReturn(Optional.of(teacherSubmission));
        when(userTaskRepository.findByUserAndTask(student, teacherTask)).thenReturn(Optional.of(studentSubmission));

        GroupStatisticsDto statistics = groupQueryService.getGroupStatistics(group, teacher);

        assertThat(statistics.getTotalStudents()).isEqualTo(2);
        assertThat(statistics.getSubmittedTasks()).isEqualTo(2);
    }

    private User teacher(Long id, String name, Group... groups) {
        User teacher = new User();
        teacher.setId(id);
        teacher.setName(name);
        teacher.setGroups(Set.of(groups));
        teacher.setRoles(Set.of(new Role("ROLE_TEACHER", "Teacher")));
        return teacher;
    }

    private User student(Long id, String name, Group group, String familyName) {
        User student = new User();
        student.setId(id);
        student.setName(name);
        student.setFamilyName(familyName);
        student.setGroups(Set.of(group));
        student.setRoles(Set.of(new Role("ROLE_STUDENT", "Student")));
        return student;
    }

    private Group group(Long id, String name) {
        Group group = new Group(name);
        group.setId(id);
        return group;
    }

    private Task task(Long id, String title, User createdBy, Group group, UnitTitle unitTitle) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setCreatedBy(createdBy);
        task.setAssignedGroups(Set.of(group));
        task.setUnitTitle(unitTitle);
        task.setIsActive(true);
        return task;
    }

    private UnitTitle unitTitle(String id, String name, int weight) {
        return new UnitTitle(id, name, name + " description", weight);
    }

    private TaskStatus status(String name) {
        return new TaskStatus(name, name, 0);
    }

    private UserTask userTask(Long id, User user, Task task, TaskStatus status, LocalDateTime lastModified) {
        UserTask userTask = new UserTask();
        userTask.setId(id);
        userTask.setUser(user);
        userTask.setTask(task);
        userTask.setStatus(status);
        userTask.setLastModified(lastModified);
        return userTask;
    }
}
