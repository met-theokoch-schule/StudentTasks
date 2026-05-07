package com.example.studenttask.config;

import com.example.studenttask.model.Role;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.repository.RoleRepository;
import com.example.studenttask.repository.TaskStatusRepository;
import com.example.studenttask.repository.TaskViewRepository;
import com.example.studenttask.service.UnitTitleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

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
            log.info("Initialized default roles");
        }
    }

    private void initializeTaskStatuses() {
        if (taskStatusRepository.count() == 0) {
            taskStatusRepository.save(new TaskStatus("NICHT_BEGONNEN", "Aufgabe wurde noch nicht begonnen", 1));
            taskStatusRepository.save(new TaskStatus("IN_BEARBEITUNG", "Aufgabe wird bearbeitet", 2));
            taskStatusRepository.save(new TaskStatus("ABGEGEBEN", "Aufgabe wurde abgegeben", 3));
            taskStatusRepository.save(new TaskStatus("ÜBERARBEITUNG_NÖTIG", "Aufgabe muss überarbeitet werden", 4));
            taskStatusRepository.save(new TaskStatus("VOLLSTÄNDIG", "Aufgabe ist vollständig abgeschlossen", 5));
            log.info("Initialized default task statuses");
        }
    }

    private void initializeTaskViews() {
        // Initialize or update task views based on unique templatePath

        createOrUpdateTaskView("HTML+CSS Editor", "taskviews/html-css-editor",
                "Erstellen von HTML-Seiten mit CSS und Bildern!", false);

        createOrUpdateTaskView("Struktogramm Editor", "taskviews/struktog",
                "Erstellen von Struktogrammen!", false);

        createOrUpdateTaskView("UML-Diagramm Editor", "taskviews/apollon-uml",
                    "Erstellen von UML-Diagrammen!", false);

        createOrUpdateTaskView("Python Editor", "taskviews/python-editor",
                "Erstellen von Python Konsolenprogrammen!", false);

        createOrUpdateTaskView("Python HTML Editor", "taskviews/python-html-editor",
                "Erstellen von Pythonprogrammen mit HTML als GUI!", false);

        createOrUpdateTaskView("Python Hamster Editor", "taskviews/python-hamster-editor",
                "Erstellen von Python-Hamster-Programmen!", false);

        createOrUpdateTaskView("Python Turtle Editor", "taskviews/python-turtle-editor",
                "Erstellen von Python-Turtle-Programmen!", false);

        createOrUpdateTaskView("Python Sortier-Editor", "taskviews/python-sorting-editor",
                "Visualisierung von Sortieralgorithmen", false);

        createOrUpdateTaskView("Python Lückentext-Editor", "taskviews/python-fillgap-editor",
                "Programmcode Lückentexte, die ausführbar sind", false);

        createOrUpdateTaskView("Code Mainia Python", "taskviews/code-mainia-python",
                "Single Player Version des Hopp Foundations Spiels", true);

        createOrUpdateTaskView("SQL Aufgaben", "taskviews/sql-task-view",
                "Interaktive SQL Aufgaben stellen", false);

        createOrUpdateTaskView("Relationen Algebra Aufgaben", "taskviews/ra-task-view",
                "Interaktive RA Aufgaben stellen", false);

        createOrUpdateTaskView("ER-Diagramm", "taskviews/erd",
                "Interaktive ERD's erstellen", false);

        createOrUpdateTaskView("Automaten (FlapJS)", "taskviews/dfa-nfa-pda",
                "DFA, NFA und Kellerautomat mit FlapJS simulieren", false);

        createOrUpdateTaskView("Automaten (Flaci)", "taskviews/flaci-automaten",
                "DEA, NEA, MEALY, MOORE, DKA, NKA, TM mit Flaci", false);


            createOrUpdateTaskView("Grammatik Editor", "taskviews/flaci-grammatik",
                    "Grammatiken und Syntaxdiagramme mit Flaci", false);
            
        createOrUpdateTaskView("Registermaschine", "taskviews/registermaschine",
                    "Registermaschinen simulieren", false);

        createOrUpdateTaskView("Turingmaschine", "taskviews/turingmaschine",
                        "Turingmaschinen simulieren", false);

        createOrUpdateTaskView("H5P Aufgaben", "taskviews/h5p",
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
