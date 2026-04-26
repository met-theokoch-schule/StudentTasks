package com.example.studenttask.controller;

import com.example.studenttask.dto.GroupOverviewDto;
import com.example.studenttask.dto.GroupStatisticsDto;
import com.example.studenttask.dto.StudentTaskMatrixDto;
import com.example.studenttask.dto.StudentTaskStatusDto;
import com.example.studenttask.exception.TeacherAuthenticationRequiredException;
import com.example.studenttask.exception.TeacherResourceNotFoundException;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.User;
import com.example.studenttask.service.GroupQueryService;
import com.example.studenttask.service.GroupService;
import com.example.studenttask.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherGroupControllerTest {

    @Mock
    private GroupService groupService;

    @Mock
    private GroupQueryService groupQueryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TeacherGroupController controller;

    @Test
    void listGroups_addsTypedGroupOverviewToModel() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        GroupOverviewDto groupOverview = new GroupOverviewDto(
            group,
            12,
            3,
            5,
            LocalDateTime.of(2026, 4, 19, 9, 0)
        );

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));
        when(groupQueryService.getGroupsWithActiveTasksByTeacher(teacher)).thenReturn(List.of(groupOverview));

        Model model = new ExtendedModelMap();
        String view = controller.listGroups(model, principal("oidc-teacher"));

        assertThat(view).isEqualTo("teacher/groups-list");
        assertThat(model.getAttribute("teacher")).isSameAs(teacher);
        assertThat(model.getAttribute("groups")).isEqualTo(List.of(groupOverview));
    }

    @Test
    void showGroupDetail_addsTypedStatisticsAndMatrixToModel() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);
        User student = student(2L, "Ada Student", group);
        Task task = task(20L, "Sorting", group);
        GroupStatisticsDto statistics = new GroupStatisticsDto(1, 1, 0, 0);

        Map<String, StudentTaskStatusDto> statusMap = new LinkedHashMap<>();
        statusMap.put(
            StudentTaskMatrixDto.statusKey(student.getId(), task.getId()),
            new StudentTaskStatusDto(status("ABGEGEBEN"), true, 30L, "fas fa-hourglass-half text-warning", "text-warning")
        );
        StudentTaskMatrixDto matrix = new StudentTaskMatrixDto(List.of(student), List.of(task), statusMap);

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));
        when(groupService.findById(10L)).thenReturn(group);
        when(groupQueryService.getGroupStatistics(group, teacher)).thenReturn(statistics);
        when(groupQueryService.getStudentTaskMatrix(group)).thenReturn(matrix);

        Model model = new ExtendedModelMap();
        String view = controller.showGroupDetail(10L, model, principal("oidc-teacher"));

        assertThat(view).isEqualTo("teacher/group-detail");
        assertThat(model.getAttribute("group")).isSameAs(group);
        assertThat(model.getAttribute("statistics")).isSameAs(statistics);
        assertThat(model.getAttribute("matrix")).isSameAs(matrix);
        assertThat(matrix.getStatus(student.getId(), task.getId())).isNotNull();
        assertThat(matrix.getStatus(student.getId(), task.getId()).getStatusIcon())
            .isEqualTo("fas fa-hourglass-half text-warning");
    }

    @Test
    void listGroups_throwsAuthenticationExceptionWhenTeacherCannotBeResolved() {
        when(userService.findByOpenIdSubject("missing-teacher")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.listGroups(new ExtendedModelMap(), principal("missing-teacher")))
            .isInstanceOf(TeacherAuthenticationRequiredException.class)
            .hasMessage("Benutzer nicht gefunden");
    }

    @Test
    void showGroupDetail_throwsNotFoundExceptionWhenGroupIsMissing() {
        Group group = group(10L, "10A");
        User teacher = teacher(1L, "Teacher", group);

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));
        when(groupService.findById(99L)).thenReturn(null);

        assertThatThrownBy(() -> controller.showGroupDetail(99L, new ExtendedModelMap(), principal("oidc-teacher")))
            .isInstanceOf(TeacherResourceNotFoundException.class)
            .hasMessage("Gruppe nicht gefunden");
    }

    private Principal principal(String name) {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(name);
        return principal;
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

    private Task task(Long id, String title, Group group) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setAssignedGroups(Set.of(group));
        task.setIsActive(true);
        return task;
    }

    private TaskStatus status(String name) {
        return new TaskStatus(name, name, 0);
    }
}
