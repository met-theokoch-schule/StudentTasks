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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherDashboardQueryServiceTest {

    private static final List<String> REVIEW_REMINDER_MESSAGES = List.of(
        "Feedback first - diese Reviews wollen heute noch dein Star-Moment sein.",
        "Einmal kurz bewerten, zweimal Wirkung entfalten.",
        "Dein Feedback called - und diese Abgaben gehen direkt ran.",
        "Mach aus offenen Reviews einfach erledigte Glücksmomente.",
        "Ein kleiner Klick für dich, ein großes Update für die Lernenden.",
        "Bewerte jetzt, bevor sich die offenen Reviews für wichtig halten.",
        "Dein Feedback-Finger hat heute definitiv noch Kapazität.",
        "Diese Reviews stehen Schlange für ein bisschen Lehrkraft-Magie.",
        "Kurz reingehen, klar bewerten, gut aussehen.",
        "Offene Reviews? Zeit für deinen souveränen Aufräum-Move.",
        "Dein nächster Bewertungs-Sprint ist nur einen Klick entfernt.",
        "Hier wartet kein Papierstapel, sondern deine nächste Feedback-Show.",
        "Gib den Abgaben das Upgrade, auf das sie heimlich hoffen.",
        "Ein paar Bewertungen jetzt und später fühlt sich alles leichter an.",
        "Heute schon brillant bewertet? Diese Reviews hätten da Interesse.",
        "Diese Reviews fahren nicht von allein ins Ziel.",
        "Noch offen? Dann wird’s Zeit für deinen Bewertungs-Endspurt.",
        "Mehr Feedback, weniger Vielleicht.",
        "Steig ein in die nächste Runde guter Rückmeldungen.",
        "Diese Abgaben warten nicht auf Wunder, sondern auf dich.",
        "Einmal kurz bewerten. Wirkt länger als jeder Kaffee.",
        "Heute schon Wissen bewegt? Dann ab an die Reviews.",
        "Offene Bewertungen sind auch nur To-dos mit Aufmerksamkeitshunger.",
        "Deine Rückmeldung bringt hier gerade mehr voran als jedes Update.",
        "Wer bewertet, führt. Wer aufschiebt, sammelt nur Spannungsbogen.",
        "Diese Reviews entern sich nicht von selbst, Käpt’n.",
        "Noch offene Bewertungen? Dann ran ans Steuer und Kurs auf erledigt.",
        "Wer Feedback bunkert, hat bald Meuterei im Postfach.",
        "Setz die Segel, sonst treibt der Review-Stapel davon.",
        "Ein guter Käpt’n lässt keine Abgabe über Bord gehen.",
        "Diese Reviews warten schon ungeduldiger als eine Crew ohne Rum.",
        "Klar zum Bewerten, sonst wird aus Ordnung schnell hohe See.",
        "Hol dir die Beute: zehn Minuten Ruhe durch ein paar erledigte Reviews.",
        "Wer jetzt bewertet, muss später keine Wrackbergung betreiben.",
        "Ran an die Reviews, bevor aus einem kleinen Wellenhüpfer ein Sturm wird.",
        "Keine Panik. Es sind nur Reviews. Leider deine.",
        "Im großen Maßstab des Universums sind das wenige Reviews. Im Dashboard eher nicht.",
        "Diese Abgaben warten mit der Ruhe eines Problems, das nicht von selbst verschwindet.",
        "Statistisch gesehen fühlt sich Bewerten besser an, als es weiter aufzuschieben.",
        "Irgendwo im Kosmos wäre das schon erledigt. Hier braucht es noch deinen Klick.",
        "Die gute Nachricht: Es sind nur Reviews. Die andere kennst du bereits.",
        "Das Universum ist chaotisch genug. Diese Bewertungen müssen es nicht auch noch sein.",
        "Wer jetzt bewertet, spart sich später eine philosophische Krise im Dashboard.",
        "Zwischen Sternenstaub und To-do-Liste ist dein Feedback überraschend relevant.",
        "Manche Fragen sind unendlich kompliziert. Diese Reviews gehören nicht dazu.",
        "Hast du schon versucht, diese Reviews einfach zu erledigen?",
        "Die gute Nachricht: Es ist kein Serverproblem. Die schlechte: Du musst wirklich selbst ran.",
        "Diese Reviews sind jetzt offiziell ein Fall für den Support der höchsten Eskalationsstufe: dich.",
        "Ich würde helfen, aber ich bin nur eine auffällig passive Erinnerungsbox.",
        "Die Reviews sind noch da. Sie haben also offenbar nicht von allein aufgelegt.",
        "Technisch gesehen wäre Bewerten jetzt die überraschend kompetente Entscheidung.",
        "Diese offenen Reviews fühlen sich schon ein bisschen zu wohl in deinem Dashboard.",
        "Es ist ein ganz normaler Arbeitstag, bis man die Zahl offener Bewertungen sieht.",
        "Gute Neuigkeiten aus der IT: Das Problem sitzt diesmal nicht unter dem Schreibtisch, sondern direkt hier.",
        "Diese Reviews könnten dringend jemanden mit Autorität und Mauszeiger gebrauchen.",
        "Niemand sagt, dass es glamourös ist. Aber sehr zufriedenstellend wäre es schon.",
        "Du bist nur ein paar Klicks davon entfernt, so zu wirken, als hättest du alles im Griff.",
        "Die Lage ist beherrschbar. Unnötig dramatisch, aber beherrschbar.",
        "Diese Bewertungen lösen sich nicht in Luft auf. Das wäre auch zu benutzerfreundlich.",
        "Ein kurzer Review-Block jetzt spart dir später dieses stille Anstarren der Zahl."
    );

    @Mock
    private TaskService taskService;

    @Mock
    private UserTaskService userTaskService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TeacherDashboardQueryService teacherDashboardQueryService;

    @Test
    void getDashboardData_returnsPendingReviewCountAndRecentTaskLimit() {
        ReflectionTestUtils.setField(teacherDashboardQueryService, "reviewReminderThreshold", 5);

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
        when(userService.hasStudentRole(student)).thenReturn(true);

        TeacherDashboardDataDto dashboardData = teacherDashboardQueryService.getDashboardData(teacher);

        assertThat(dashboardData.getPendingReviews()).isEqualTo(1);
        assertThat(dashboardData.getRecentTasks()).containsExactlyElementsOf(recentTasks.subList(0, 5));
        assertThat(dashboardData.isShowReviewReminder()).isFalse();
        assertThat(dashboardData.getReviewReminderMessage()).isNull();
    }

    @Test
    void getDashboardData_enablesRandomReviewReminderAboveThreshold() {
        ReflectionTestUtils.setField(teacherDashboardQueryService, "reviewReminderThreshold", 5);

        Group sharedGroup = group(51L, "Q2");
        User teacher = teacher(1L, "Teacher", sharedGroup);
        User student = student(10L, "Student", sharedGroup);

        Task pendingTask = task(101L, "Pending Task", teacher, sharedGroup, null);
        List<UserTask> submittedUserTasks = List.of(
            userTask(student, pendingTask, status("ABGEGEBEN")),
            userTask(student, pendingTask, status("ABGEGEBEN")),
            userTask(student, pendingTask, status("ABGEGEBEN")),
            userTask(student, pendingTask, status("ABGEGEBEN")),
            userTask(student, pendingTask, status("ABGEGEBEN")),
            userTask(student, pendingTask, status("ABGEGEBEN"))
        );

        when(taskService.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher)).thenReturn(List.of(pendingTask));
        when(userTaskService.findByTask(pendingTask)).thenReturn(submittedUserTasks);
        when(taskService.findByCreatedByOrderByCreatedAtDesc(teacher)).thenReturn(List.of(pendingTask));
        when(userService.hasStudentRole(student)).thenReturn(true);

        TeacherDashboardDataDto dashboardData = teacherDashboardQueryService.getDashboardData(teacher);

        assertThat(dashboardData.isShowReviewReminder()).isTrue();
        assertThat(REVIEW_REMINDER_MESSAGES).contains(dashboardData.getReviewReminderMessage());
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
        when(userService.hasStudentRole(matchingStudent)).thenReturn(true);
        when(userService.hasStudentRole(nonMatchingStudent)).thenReturn(true);

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
        when(userService.hasStudentRole(sharedStudent)).thenReturn(true);
        when(userService.hasStudentRole(secondSharedStudent)).thenReturn(true);
        when(userService.hasStudentRole(foreignStudent)).thenReturn(true);

        assertThat(teacherDashboardQueryService.countPendingReviews(teacher)).isEqualTo(2);
    }

    @Test
    void countPendingReviews_includesTeacherMembersWhenConfiguredForLocalViews() {
        Group sharedGroup = group(51L, "Q2");
        User teacher = teacher(1L, "Teacher", sharedGroup);

        Task task = task(301L, "Task 1", teacher, sharedGroup, null);
        UserTask submittedTeacherTask = userTask(teacher, task, status("ABGEGEBEN"));

        ReflectionTestUtils.setField(teacherDashboardQueryService, "includeTeachersInTeacherViews", true);

        when(taskService.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher)).thenReturn(List.of(task));
        when(userTaskService.findByTask(task)).thenReturn(List.of(submittedTeacherTask));
        when(userService.hasStudentRole(teacher)).thenReturn(false);
        when(userService.hasTeacherRole(teacher)).thenReturn(true);

        assertThat(teacherDashboardQueryService.countPendingReviews(teacher)).isEqualTo(1);
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
