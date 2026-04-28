package com.example.studenttask.service;

import com.example.studenttask.exception.TeacherAuthenticationRequiredException;
import com.example.studenttask.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherIdentityServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private TeacherIdentityService teacherIdentityService;

    @Test
    void requireTeacher_resolvesTeacherBySubject() {
        User teacher = new User();
        teacher.setId(7L);

        when(userService.findByOpenIdSubject("oidc-teacher")).thenReturn(Optional.of(teacher));

        assertThat(teacherIdentityService.requireTeacher("oidc-teacher")).isSameAs(teacher);
    }

    @Test
    void requireTeacher_throwsWhenTeacherCannotBeResolvedBySubject() {
        when(userService.findByOpenIdSubject("missing-teacher")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherIdentityService.requireTeacher("missing-teacher"))
            .isInstanceOf(TeacherAuthenticationRequiredException.class)
            .hasMessage("Benutzer nicht gefunden");
    }

    @Test
    void requireTeacher_throwsWhenTeacherObjectIsMissing() {
        assertThatThrownBy(() -> teacherIdentityService.requireTeacher((User) null))
            .isInstanceOf(TeacherAuthenticationRequiredException.class)
            .hasMessage("Benutzer nicht gefunden");
    }
}
