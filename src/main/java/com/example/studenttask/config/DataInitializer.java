package com.example.studenttask.config;

import com.example.studenttask.model.Role;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.repository.RoleRepository;
import com.example.studenttask.repository.TaskStatusRepository;
import com.example.studenttask.repository.TaskViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import com.example.studenttask.service.UnitTitleService;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskViewRepository taskViewRepository;

    @Autowired
    private UnitTitleService unitTitleService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeRoles();
        initializeTaskStatuses();
        initializeTaskViews();
        initializeUnitTitles();
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("ROLE_STUDENT", "Schüler - kann Aufgaben bearbeiten und abgeben"));
            roleRepository.save(new Role("ROLE_TEACHER", "Lehrer - kann Aufgaben erstellen und bewerten"));
            roleRepository.save(new Role("ROLE_ADMIN", "Administrator - kann System verwalten"));
            System.out.println("Default roles initialized");
        }
    }

    private void initializeTaskStatuses() {
        if (taskStatusRepository.count() == 0) {
            taskStatusRepository.save(new TaskStatus("NICHT_BEGONNEN", "Aufgabe wurde noch nicht begonnen", 1));
            taskStatusRepository.save(new TaskStatus("IN_BEARBEITUNG", "Aufgabe wird bearbeitet", 2));
            taskStatusRepository.save(new TaskStatus("ABGEGEBEN", "Aufgabe wurde abgegeben", 3));
            taskStatusRepository.save(new TaskStatus("ÜBERARBEITUNG_NÖTIG", "Aufgabe muss überarbeitet werden", 4));
            taskStatusRepository.save(new TaskStatus("VOLLSTÄNDIG", "Aufgabe ist vollständig abgeschlossen", 5));
            System.out.println("Default task statuses initialized");
        }
    }

    private void initializeTaskViews() {
        // Initialize or update task views based on unique templatePath

        String htmlCssEditor = "taskviews/html-css-editor";

        createOrUpdateTaskView("HTML+CSS Editor", htmlCssEditor,
                "Erstellen von HTML-Seiten mit CSS und Bildern!", false);

        String struktogEditor = "taskviews/struktog";

        createOrUpdateTaskView("Struktogramm Editor", struktogEditor,
                "Erstellen von Struktogrammen!", false);

        String pythonEditor = "taskviews/python-editor";

        createOrUpdateTaskView("Python Editor", pythonEditor,
                "Erstellen von Python Konsolenprogrammen!", false);

        String pythonHtmlEditor = "taskviews/python-html-editor";

        createOrUpdateTaskView("Python HTML Editor", pythonHtmlEditor,
                "Erstellen von Pythonprogrammen mit HTML als GUI!", false);

        String pythonHamsterEditor = "taskviews/python-hamster-editor";

        createOrUpdateTaskView("Python Hamster Editor", pythonHamsterEditor,
                "Erstellen von Python-Hamster-Programmen!", false);

        String pythonSortingEditor = "taskviews/python-sorting-editor";

        createOrUpdateTaskView("Python Sortier-Editor", pythonSortingEditor,
                "Visualisierung von Sortieralgorithmen", false);

            String codemainia = "taskviews/code-mainia-python";

            createOrUpdateTaskView("Code Mainia Python", codemainia,
                    "Single Player Version des Hopp Foundations Spiels", true);

        String sqlTaskView = "taskviews/sql-task-view";

        createOrUpdateTaskView("SQL Aufgaben", sqlTaskView,
                "Interaktive SQL Aufgaben stellen", false);

        String raTaskView = "taskviews/ra-task-view";

        createOrUpdateTaskView("Relationen Algebra Aufgaben", raTaskView,
                "Interaktive RA Aufgaben stellen", false);

        createOrUpdateTaskView("ER-Diagramm", "taskviews/erd",
                "Interaktive ERD's erstellen", false);

        String h5pTaskView = "taskviews/h5p";

        createOrUpdateTaskView("H5P Aufgaben", h5pTaskView,
                "Einbetten von H5P-Inhalten per iframe", true);

        // Hier können weitere TaskViews hinzugefügt werden
        // Beispiel für weitere TaskViews:
        /*
         * String htmlEditorTemplatePath = "taskviews/html-editor";
         * createOrUpdateTaskView("HTML Editor", htmlEditorTemplatePath,
         * "Rich-Text HTML Editor für Textaufgaben", false);
         */
    }

    private void createOrUpdateTaskView(String name, String templatePath, String description,
            boolean submitMarksComplete) {
        TaskView taskView = taskViewRepository.findByTemplatePath(templatePath);
        if (taskView == null) {
            taskView = new TaskView(name, templatePath);
        }
        taskView.setName(name);
        taskView.setDescription(description);
        taskView.setTemplatePath(templatePath);
        taskView.setSubmitMarksComplete(submitMarksComplete);
        taskViewRepository.save(taskView);
    }

    private void initializeUnitTitles() {
        // Unit Titles mit Gewichtung für die Anzeige-Reihenfolge
        // Niedrigere weight-Werte werden zuerst angezeigt
        unitTitleService.createOrUpdate("html-css",
                "HTML & CSS",
                "Webentwicklung mit HTML und CSS",
                10);

        unitTitleService.createOrUpdate("einfuehrung-programmierung",
                "Einführung in die Programmierung",
                "Grundlagen der Programmierung und erste Schritte",
                20);

        unitTitleService.createOrUpdate("algorithmen",
                "Algorithmik",
                "Such- und Sortieralgorithmen",
                30);

        unitTitleService.createOrUpdate("objektorientierung",
                "Objektorientierte Programmierung",
                "Klassen und Objekte",
                40);

        unitTitleService.createOrUpdate("datenbanken",
                    "Datenbanken",
                    "Relationenalgebra, ER-Diagramme und SQL",
                    50);

        // unitTitleService.createOrUpdate("datenbanken",
        // "Datenbanken",
        // "Grundlagen von Datenbanken und SQL",
        // 50);

        // unitTitleService.createOrUpdate("automatentheorie",
        // "Automatentheorie",
        // "DFA, NFA",
        // 60);

        // Weitere Unit Titles können hier hinzugefügt werden
        // Die Gewichtung bestimmt die Reihenfolge auf der Schüler-Seite
    }
}
