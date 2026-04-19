package com.example.studenttask.service;

import com.example.studenttask.dto.GroupOverviewDto;
import com.example.studenttask.dto.GroupStatisticsDto;
import com.example.studenttask.dto.StudentTaskMatrixDto;
import com.example.studenttask.dto.StudentTaskStatusDto;
import com.example.studenttask.model.Group;
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

    @InjectMocks
    private GroupQueryService groupQueryService;

    @Test
    void getGroupsWithActiveTasksByTeacher_countsPendingSubmissionsAndLastActivity() {
        Group activeGroup = group(10L, "10A");
        Group emptyGroup = group(11L, "11B");
        User teacher = teacher(1L, "Teacher", activeGroup, emptyGroup);
        User studentStarted = student(2L, "Ada", activeGroup, "Lovelace");
        User studentMissing = student(3L, "Grace", activeGroup, "Hopper");

        Task task = task(20L, "Queries", teacher, activeGroup, unitTitle("sql", "SQL", 10));
        UserTask startedTask = userTask(30L, studentStarted, task, status("IN_BEARBEITUNG"),
            LocalDateTime.of(2026, 4, 19, 10, 30));

        when(taskRepository.findByIsActiveTrueOrderByCreatedAtDesc()).thenReturn(List.of(task));
        when(userRepository.countByGroupsContaining(activeGroup)).thenReturn(2);
        when(userRepository.findByGroupsContaining(activeGroup)).thenReturn(List.of(studentStarted, studentMissing));
        when(userTaskRepository.findByUserAndTask(studentStarted, task)).thenReturn(Optional.of(startedTask));
        when(userTaskRepository.findByUserAndTask(studentMissing, task)).thenReturn(Optional.empty());

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
    void getStudentTaskMatrix_sortsStudentsAndTasksAndBuildsTypedStatuses() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        User secondStudent = student(2L, "Berta", group, "Zimmer");
        User firstStudent = student(3L, "Anna", group, "Alpha");

        Task laterTask = task(20L, "B Task", teacher, group, unitTitle("advanced", "Advanced", 20));
        Task earlierTask = task(21L, "A Task", teacher, group, unitTitle("basics", "Basics", 10));
        UserTask submittedTask = userTask(40L, firstStudent, earlierTask, status("ABGEGEBEN"), null);

        when(userRepository.findByGroupsContaining(group)).thenReturn(List.of(secondStudent, firstStudent));
        when(taskRepository.findByIsActiveTrue()).thenReturn(List.of(laterTask, earlierTask));
        when(userTaskRepository.findByUserAndTask(firstStudent, earlierTask)).thenReturn(Optional.of(submittedTask));
        when(userTaskRepository.findByUserAndTask(firstStudent, laterTask)).thenReturn(Optional.empty());
        when(userTaskRepository.findByUserAndTask(secondStudent, earlierTask)).thenReturn(Optional.empty());
        when(userTaskRepository.findByUserAndTask(secondStudent, laterTask)).thenReturn(Optional.empty());
        when(taskContentRepository.countByUserTaskAndIsSubmittedTrue(submittedTask)).thenReturn(2);

        StudentTaskMatrixDto matrix = groupQueryService.getStudentTaskMatrix(group);

        assertThat(matrix.getStudents()).containsExactly(firstStudent, secondStudent);
        assertThat(matrix.getTasks()).containsExactly(earlierTask, laterTask);

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
    void getGroupStatistics_countsOnlyTeacherOwnedStatusesForGroup() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        User studentA = student(2L, "Ada", group, "Lovelace");
        User studentB = student(3L, "Grace", group, "Hopper");

        Task teacherTask = task(20L, "Teacher Task", teacher, group, null);
        Task completedTask = task(21L, "Completed Task", teacher, group, null);

        UserTask submitted = userTask(30L, studentA, teacherTask, status("ABGEGEBEN"), null);
        UserTask revision = userTask(31L, studentB, teacherTask, status("UEBERARBEITUNG_NOETIG"), null);
        UserTask complete = userTask(32L, studentA, completedTask, status("VOLLSTAENDIG"), null);

        when(userRepository.countByGroupsContaining(group)).thenReturn(2);
        when(taskRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(teacher, true))
            .thenReturn(List.of(teacherTask, completedTask));
        when(userRepository.findByGroupsContaining(group)).thenReturn(List.of(studentA, studentB));
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

    private User teacher(Long id, String name, Group... groups) {
        User teacher = new User();
        teacher.setId(id);
        teacher.setName(name);
        teacher.setGroups(Set.of(groups));
        return teacher;
    }

    private User student(Long id, String name, Group group, String familyName) {
        User student = new User();
        student.setId(id);
        student.setName(name);
        student.setFamilyName(familyName);
        student.setGroups(Set.of(group));
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
