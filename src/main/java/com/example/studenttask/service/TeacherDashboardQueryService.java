package com.example.studenttask.service;

import com.example.studenttask.dto.TeacherDashboardDataDto;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.Task;
import com.example.studenttask.model.TaskStatusCode;
import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.model.User;
import com.example.studenttask.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class TeacherDashboardQueryService {

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

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private UserService userService;

    @Value("${app.teacher.views.include-teachers:false}")
    private boolean includeTeachersInTeacherViews;

    @Value("${app.teacher.review-reminder-threshold:5}")
    private int reviewReminderThreshold;

    public TeacherDashboardDataDto getDashboardData(User teacher) {
        int pendingReviews = countPendingReviews(teacher);
        List<Task> recentTasks = taskService.findByCreatedByOrderByCreatedAtDesc(teacher)
            .stream()
            .limit(5)
            .collect(Collectors.toList());
        boolean showReviewReminder = pendingReviews > reviewReminderThreshold;
        String reviewReminderMessage = showReviewReminder ? randomReviewReminderMessage() : null;

        return new TeacherDashboardDataDto(
            pendingReviews,
            recentTasks,
            showReviewReminder,
            reviewReminderMessage
        );
    }

    public Map<UnitTitle, Map<Task, List<UserTask>>> getGroupedPendingReviews(User teacher) {
        List<Task> teacherTasks = taskService.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher);
        Map<UnitTitle, Map<Task, List<UserTask>>> groupedPendingReviews = new LinkedHashMap<>();

        for (Task task : teacherTasks) {
            List<UserTask> pendingUserTasks = findPendingUserTasksForTask(teacher, task);
            if (!pendingUserTasks.isEmpty()) {
                groupedPendingReviews.computeIfAbsent(task.getUnitTitle(), ignored -> new LinkedHashMap<>())
                    .put(task, pendingUserTasks);
            }
        }

        return groupedPendingReviews;
    }

    public int countPendingReviews(User teacher) {
        return taskService.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(teacher)
            .stream()
            .mapToInt(task -> findPendingUserTasksForTask(teacher, task).size())
            .sum();
    }

    private List<UserTask> findPendingUserTasksForTask(User teacher, Task task) {
        Set<Group> teacherGroups = safeGroups(teacher);
        Set<Group> assignedGroups = safeGroups(task.getAssignedGroups());
        List<UserTask> pendingUserTasks = new ArrayList<>();

        for (UserTask userTask : userTaskService.findByTask(task)) {
            if (!TaskStatusSupport.hasCode(userTask.getStatus(), TaskStatusCode.ABGEGEBEN)) {
                continue;
            }

            if (!shouldIncludePendingReviewUser(userTask.getUser())) {
                continue;
            }

            if (sharesAssignedGroup(teacherGroups, safeGroups(userTask.getUser()), assignedGroups)) {
                pendingUserTasks.add(userTask);
            }
        }

        return pendingUserTasks;
    }

    private boolean sharesAssignedGroup(Set<Group> teacherGroups, Set<Group> studentGroups, Set<Group> assignedGroups) {
        for (Group assignedGroup : assignedGroups) {
            if (teacherGroups.contains(assignedGroup) && studentGroups.contains(assignedGroup)) {
                return true;
            }
        }
        return false;
    }

    private Set<Group> safeGroups(User user) {
        return user == null ? Collections.emptySet() : safeGroups(user.getGroups());
    }

    private Set<Group> safeGroups(Set<Group> groups) {
        return groups == null ? Collections.emptySet() : new LinkedHashSet<>(groups);
    }

    private boolean shouldIncludePendingReviewUser(User user) {
        return userService.hasStudentRole(user)
            || (includeTeachersInTeacherViews && userService.hasTeacherRole(user));
    }

    private String randomReviewReminderMessage() {
        return REVIEW_REMINDER_MESSAGES.get(ThreadLocalRandom.current().nextInt(REVIEW_REMINDER_MESSAGES.size()));
    }
}
