package com.example.studenttask.controller;

import com.example.studenttask.dto.TeacherDashboardDataDto;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import com.example.studenttask.service.TeacherDashboardQueryService;
import com.example.studenttask.service.TeacherTaskCommandService;
import com.example.studenttask.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private TeacherDashboardQueryService teacherDashboardQueryService;

    @Mock
    private TeacherTaskCommandService teacherTaskCommandService;

    @InjectMocks
    private TeacherController controller;

    @Test
    void dashboard_returnsTeacherDashboardWithPendingReviewCountAndRecentTaskLimit() {
        Group sharedGroup = group(51L, "Q2");
        User teacher = teacher(1L, "Teacher", sharedGroup);
        User student = student(10L, "Student", sharedGroup);

        Task pendingTask = task(101L, "Pending Task", teacher, sharedGroup, null);
        UserTask submittedUserTask = userTask(student, pendingTask, status("ABGEGEBEN"));

        List<Task> recentTasks = new ArrayList<>();
        for (long i = 1; i <= 6; i++) {
            recentTasks.add(task(200L + i, "Recent " + i, teacher, sharedGroup, null));
        }

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));
        when(teacherDashboardQueryService.getDashboardData(teacher))
            .thenReturn(new TeacherDashboardDataDto(1, recentTasks.subList(0, 5)));

        Model model = new ExtendedModelMap();
        String view = controller.dashboard(model, principal("oidc-teacher"));

        assertThat(view).isEqualTo("teacher/dashboard");
        assertThat(model.getAttribute("pendingReviews")).isEqualTo(1);

        @SuppressWarnings("unchecked")
        List<Task> recentTasksFromModel = (List<Task>) model.getAttribute("recentTasks");
        assertThat(recentTasksFromModel).containsExactlyElementsOf(recentTasks.subList(0, 5));
    }

    @Test
    void pendingReviews_groupsOnlySubmittedTasksWithSharedGroupMembership() {
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

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));
        Map<UnitTitle, Map<Task, List<UserTask>>> groupedPendingReviews = new LinkedHashMap<>();
        groupedPendingReviews.put(unitTitle, new LinkedHashMap<>(Map.of(task, List.of(submittedMatching))));
        when(teacherDashboardQueryService.getGroupedPendingReviews(teacher)).thenReturn(groupedPendingReviews);

        Model model = new ExtendedModelMap();
        Authentication authentication = authentication("oidc-teacher");
        String view = controller.pendingReviews(model, authentication);

        assertThat(view).isEqualTo("teacher/pending-reviews");

        @SuppressWarnings("unchecked")
        Map<UnitTitle, Map<Task, List<UserTask>>> groupedPendingReviewsFromModel =
                (Map<UnitTitle, Map<Task, List<UserTask>>>) model.getAttribute("groupedPendingReviews");

        Map<Task, List<UserTask>> byTask = groupedPendingReviewsFromModel.get(unitTitle);
        assertThat(byTask).isNotNull();
        assertThat(byTask.get(task)).containsExactly(submittedMatching);
    }

    @Test
    void createTask_delegatesToTeacherTaskCommandService() {
        Group group = group(51L, "Q2");
        User teacher = teacher(1L, "Teacher", group);
        Task task = new Task();

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));

        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = controller.createTask(task, List.of(51L), 5L, "sql", redirectAttributes, principal("oidc-teacher"));

        assertThat(view).isEqualTo("redirect:/teacher/tasks");
        assertThat(redirectAttributes.getFlashAttributes().get("success")).isEqualTo("Aufgabe wurde erfolgreich erstellt.");
        verify(teacherTaskCommandService).createTask(task, teacher, "5", "sql", List.of(51L));
    }

    @Test
    void saveDraft_delegatesToTeacherTaskCommandService() {
        Group group = group(51L, "Q2");
        User teacher = teacher(1L, "Teacher", group);
        Task task = new Task();

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));

        var response = controller.saveDraft(task, List.of(51L), 5L, principal("oidc-teacher"));

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo("Entwurf erfolgreich gespeichert");
        assertThat(task.getIsActive()).isFalse();
        verify(teacherTaskCommandService).createTask(task, teacher, "5", null, List.of(51L));
    }

    @Test
    void deleteTask_usesTeacherScopedCommandServiceAndAddsSuccessFlash() {
        Group group = group(51L, "Q2");
        User teacher = teacher(1L, "Teacher", group);

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));

        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = controller.deleteTask(301L, redirectAttributes, principal("oidc-teacher"));

        assertThat(view).isEqualTo("redirect:/teacher/tasks");
        assertThat(redirectAttributes.getFlashAttributes().get("success")).isEqualTo("Aufgabe wurde erfolgreich gelöscht.");
        verify(teacherTaskCommandService).deleteTask(301L, teacher);
    }

    @Test
    void viewSubmissionContent_redirectsToCanonicalTeacherTaskRoute() {
        String view = controller.viewSubmissionContent(44L, 3);

        assertThat(view).isEqualTo("redirect:/teacher/submissions/44/view?version=3");
    }

    private Principal principal(String name) {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(name);
        return principal;
    }

    private Authentication authentication(String name) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(name);
        return authentication;
    }

    private User teacher(Long id, String name, Group group) {
        User teacher = new User();
        teacher.setId(id);
        teacher.setName(name);
        teacher.setGroups(Set.of(group));
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
