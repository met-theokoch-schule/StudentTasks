package com.example.studenttask.service;

import com.example.studenttask.dto.TeacherDashboardDataDto;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherDashboardQueryServiceTest {

    @Mock
    private TaskService taskService;

    @Mock
    private UserTaskService userTaskService;

    @InjectMocks
    private TeacherDashboardQueryService teacherDashboardQueryService;

    @Test
    void getDashboardData_returnsPendingReviewCountAndRecentTaskLimit() {
        Group sharedGroup = group(51L, "Q2");
        User teacher = teacher(1L, "Teacher", sharedGroup);
        User student = student(10L, "Student", sharedGroup);

        Task pendingTask = task(101L, "Pending Task", teacher, sharedGroup, null);
        UserTask submittedUserTask = userTask(student, pendingTask, status("ABGEGEBEN"));

        List<Task> recentTasks = new ArrayList<>();
        for (long i = 1; i <= 6; i++) {
            recentTasks.add(task(200L + i, "Recent " + i, teacher, sharedGroup, null));
        }

        when(taskService.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher)).thenReturn(List.of(pendingTask));
        when(userTaskService.findByTask(pendingTask)).thenReturn(List.of(submittedUserTask));
        when(taskService.findByCreatedByOrderByCreatedAtDesc(teacher)).thenReturn(recentTasks);

        TeacherDashboardDataDto dashboardData = teacherDashboardQueryService.getDashboardData(teacher);

        assertThat(dashboardData.getPendingReviews()).isEqualTo(1);
        assertThat(dashboardData.getRecentTasks()).containsExactlyElementsOf(recentTasks.subList(0, 5));
    }

    @Test
    void getGroupedPendingReviews_groupsOnlySubmittedTasksWithSharedGroupMembership() {
        Group sharedGroup = group(51L, "Q2");
        Group otherGroup = group(52L, "Other");
        UnitTitle unitTitle = new UnitTitle("sql", "SQL", "SQL tasks", 30);

        User teacher = teacher(1L, "Teacher", sharedGroup);
        User matchingStudent = student(10L, "Student A", sharedGroup);
        User nonMatchingStudent = student(11L, "Student B", otherGroup);

        Task task = task(301L, "Query Task", teacher, sharedGroup, unitTitle);
        UserTask submittedMatching = userTask(matchingStudent, task, status("ABGEGEBEN"));
        UserTask submittedNonMatching = userTask(nonMatchingStudent, task, status("ABGEGEBEN"));
        UserTask inProgressMatching = userTask(matchingStudent, task, status("IN_BEARBEITUNG"));

        when(taskService.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher)).thenReturn(List.of(task));
        when(userTaskService.findByTask(task)).thenReturn(List.of(submittedMatching, submittedNonMatching, inProgressMatching));

        Map<UnitTitle, Map<Task, List<UserTask>>> groupedPendingReviews =
            teacherDashboardQueryService.getGroupedPendingReviews(teacher);

        Map<Task, List<UserTask>> byTask = groupedPendingReviews.get(unitTitle);
        assertThat(byTask).isNotNull();
        assertThat(byTask.get(task)).containsExactly(submittedMatching);
    }

    @Test
    void countPendingReviews_countsOnlyTasksWithSharedAssignedGroup() {
        Group sharedGroup = group(51L, "Q2");
        Group secondSharedGroup = group(53L, "Q3");
        Group otherGroup = group(52L, "Other");

        User teacher = teacher(1L, "Teacher", sharedGroup, secondSharedGroup);
        User sharedStudent = student(10L, "Student A", sharedGroup);
        User secondSharedStudent = student(11L, "Student B", secondSharedGroup);
        User foreignStudent = student(12L, "Student C", otherGroup);

        Task taskOne = task(301L, "Task 1", teacher, sharedGroup, null);
        Task taskTwo = task(302L, "Task 2", teacher, secondSharedGroup, null);

        when(taskService.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher)).thenReturn(List.of(taskOne, taskTwo));
        when(userTaskService.findByTask(taskOne)).thenReturn(List.of(
            userTask(sharedStudent, taskOne, status("ABGEGEBEN")),
            userTask(foreignStudent, taskOne, status("ABGEGEBEN"))
        ));
        when(userTaskService.findByTask(taskTwo)).thenReturn(List.of(
            userTask(secondSharedStudent, taskTwo, status("ABGEGEBEN")),
            userTask(sharedStudent, taskTwo, status("IN_BEARBEITUNG"))
        ));

        assertThat(teacherDashboardQueryService.countPendingReviews(teacher)).isEqualTo(2);
    }

    private User teacher(Long id, String name, Group... groups) {
        User teacher = new User();
        teacher.setId(id);
        teacher.setName(name);
        teacher.setGroups(Set.of(groups));
        return teacher;
    }

    private User student(Long id, String name, Group group) {
        User student = new User();
        student.setId(id);
        student.setName(name);
        student.setGroups(Set.of(group));
        return student;
    }

    private Group group(Long id, String name) {
        Group group = new Group(name);
        group.setId(id);
        return group;
    }

    private Task task(Long id, String title, User teacher, Group group, UnitTitle unitTitle) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setCreatedBy(teacher);
        task.setAssignedGroups(Set.of(group));
        task.setUnitTitle(unitTitle);
        task.setIsActive(true);
        return task;
    }

    private TaskStatus status(String name) {
        return new TaskStatus(name, name, 0);
    }

    private UserTask userTask(User student, Task task, TaskStatus status) {
        UserTask userTask = new UserTask();
        userTask.setUser(student);
        userTask.setTask(task);
        userTask.setStatus(status);
        return userTask;
    }
}
