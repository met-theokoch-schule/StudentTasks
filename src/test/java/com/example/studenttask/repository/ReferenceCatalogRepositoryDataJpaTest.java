package com.example.studenttask.repository;

import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.UnitTitle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:sqlite:file:reference-catalog-repository-test?mode=memory&cache=shared",
    "spring.datasource.driver-class-name=org.sqlite.JDBC",
    "spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReferenceCatalogRepositoryDataJpaTest {

    @Autowired
    private TaskViewRepository taskViewRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UnitTitleRepository unitTitleRepository;

    @Test
    void taskViewRepository_ordersViewsByNameAndFiltersInactiveViews() {
        taskViewRepository.saveAndFlush(taskView("SQL Aufgaben", "taskviews/sql-task-view", false, true));
        taskViewRepository.saveAndFlush(taskView("Code Mainia Python", "taskviews/code-mainia-python", true, true));
        taskViewRepository.saveAndFlush(taskView("Archivierte Ansicht", "taskviews/archive", true, false));

        assertThat(taskViewRepository.findAllByOrderByName())
            .extracting(TaskView::getName)
            .containsExactly("Archivierte Ansicht", "Code Mainia Python", "SQL Aufgaben");

        assertThat(taskViewRepository.findByIsActiveOrderByName(true))
            .extracting(TaskView::getName, TaskView::getTemplatePath, TaskView::getSubmitMarksComplete)
            .containsExactly(
                org.assertj.core.groups.Tuple.tuple("Code Mainia Python", "taskviews/code-mainia-python", true),
                org.assertj.core.groups.Tuple.tuple("SQL Aufgaben", "taskviews/sql-task-view", false)
            );

        TaskView codeMainia = taskViewRepository.findByTemplatePath("taskviews/code-mainia-python");
        assertThat(codeMainia).isNotNull();
        assertThat(codeMainia.getSubmitMarksComplete()).isTrue();
    }

    @Test
    void taskStatusRepository_returnsOrderedActiveStatusesAndFiltersAllowedNames() {
        taskStatusRepository.saveAndFlush(taskStatus("ABGEGEBEN-REF", 30, true));
        taskStatusRepository.saveAndFlush(taskStatus("IN_BEARBEITUNG-REF", 10, true));
        taskStatusRepository.saveAndFlush(taskStatus("VOLLSTAENDIG-REF", 20, true));
        taskStatusRepository.saveAndFlush(taskStatus("ARCHIVIERT-REF", 40, false));

        assertThat(taskStatusRepository.findByIsActiveTrueOrderByOrderAsc())
            .extracting(TaskStatus::getName)
            .containsExactly("IN_BEARBEITUNG-REF", "VOLLSTAENDIG-REF", "ABGEGEBEN-REF");

        assertThat(taskStatusRepository.findByNameInAndIsActiveTrue(Set.of(
            "IN_BEARBEITUNG-REF",
            "ARCHIVIERT-REF",
            "VOLLSTAENDIG-REF"
        )))
            .extracting(TaskStatus::getName)
            .containsExactlyInAnyOrder("IN_BEARBEITUNG-REF", "VOLLSTAENDIG-REF");
    }

    @Test
    void unitTitleRepository_ordersActiveTitlesByWeightThenName() {
        unitTitleRepository.saveAndFlush(unitTitle("sorting", "Sortieren", 30, true));
        unitTitleRepository.saveAndFlush(unitTitle("algorithms", "Algorithmen", 20, true));
        unitTitleRepository.saveAndFlush(unitTitle("arrays", "Arrays", 20, true));
        unitTitleRepository.saveAndFlush(unitTitle("archive", "Archiv", 10, false));

        assertThat(unitTitleRepository.findByIsActiveTrueOrderByWeightAscNameAsc())
            .extracting(UnitTitle::getId, UnitTitle::getName, UnitTitle::getWeight)
            .containsExactly(
                org.assertj.core.groups.Tuple.tuple("algorithms", "Algorithmen", 20),
                org.assertj.core.groups.Tuple.tuple("arrays", "Arrays", 20),
                org.assertj.core.groups.Tuple.tuple("sorting", "Sortieren", 30)
            );
    }

    private TaskView taskView(String name, String templatePath, boolean submitMarksComplete, boolean active) {
        TaskView taskView = new TaskView(name, templatePath);
        taskView.setSubmitMarksComplete(submitMarksComplete);
        taskView.setActive(active);
        return taskView;
    }

    private TaskStatus taskStatus(String name, int order, boolean active) {
        TaskStatus taskStatus = new TaskStatus(name, name, order);
        taskStatus.setIsActive(active);
        return taskStatus;
    }

    private UnitTitle unitTitle(String id, String name, int weight, boolean active) {
        UnitTitle unitTitle = new UnitTitle(id, name, name + " Beschreibung", weight);
        unitTitle.setActive(active);
        return unitTitle;
    }
}
