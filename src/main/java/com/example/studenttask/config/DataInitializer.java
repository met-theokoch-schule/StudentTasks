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
        // Initialize Task Views if they don't exist based on unique templatePath
        /*
         * String simpleTextTemplatePath = "taskviews/simple-text";
         * 
         * if (taskViewRepository.findByTemplatePath(simpleTextTemplatePath) == null) {
         * TaskView simpleText = new TaskView("Einfacher Texteditor",
         * simpleTextTemplatePath);
         * simpleText.setDescription("Einfaches Textfeld für Text-Abgaben");
         * taskViewRepository.save(simpleText);
         * System.out.println("TaskView with templatePath '" + simpleTextTemplatePath +
         * "' initialized");
         * }
         */

        String htmlCssEditor = "taskviews/html-css-editor";

        if (taskViewRepository.findByTemplatePath(htmlCssEditor) == null) {
            TaskView htmlCSS = new TaskView("HTML+CSS Editor", htmlCssEditor);
            htmlCSS.setDescription("Erstellen von HTML-Seiten mit CSS und Bildern!");
            taskViewRepository.save(htmlCSS);
            System.out.println("TaskView with templatePath '" + htmlCssEditor + "' initialized");
        }

        String struktogEditor = "taskviews/struktog";

        if (taskViewRepository.findByTemplatePath(struktogEditor) == null) {
            TaskView struktog = new TaskView("Struktogramm Editor", struktogEditor);
            struktog.setDescription("Erstellen von Struktogrammen!");
            taskViewRepository.save(struktog);
            System.out.println("TaskView with templatePath '" + struktogEditor + "' initialized");
        }

        String pythonEditor = "taskviews/python-editor";

        if (taskViewRepository.findByTemplatePath(pythonEditor) == null) {
            TaskView python = new TaskView("Python Editor", pythonEditor);
            python.setDescription("Erstellen von Python Konsolenprogrammen!");
            taskViewRepository.save(python);
            System.out.println("TaskView with templatePath '" + pythonEditor + "' initialized");
        }

        String pythonHtmlEditor = "taskviews/python-html-editor";

        if (taskViewRepository.findByTemplatePath(pythonHtmlEditor) == null) {
            TaskView pythonHtml = new TaskView("Python HTML Editor", pythonHtmlEditor);
            pythonHtml.setDescription("Erstellen von Pythonprogrammen mit HTML als GUI!");
            taskViewRepository.save(pythonHtml);
            System.out.println("TaskView with templatePath '" + pythonHtmlEditor + "' initialized");
        }

        String pythonHamsterEditor = "taskviews/python-hamster-editor";

        if (taskViewRepository.findByTemplatePath(pythonHamsterEditor) == null) {
            TaskView pythonHamster = new TaskView("Python Hamster Editor", pythonHamsterEditor);
            pythonHamster.setDescription("Erstellen von Python-Hamster-Programmen!");
            taskViewRepository.save(pythonHamster);
            System.out.println("TaskView with templatePath '" + pythonHamsterEditor + "' initialized");
        }

        // Hier können weitere TaskViews hinzugefügt werden
        // Beispiel für weitere TaskViews:
        /*
         * String htmlEditorTemplatePath = "taskviews/html-editor";
         * if (taskViewRepository.findByTemplatePath(htmlEditorTemplatePath) == null) {
         * TaskView htmlEditor = new TaskView("HTML Editor", htmlEditorTemplatePath);
         * htmlEditor.setDescription("Rich-Text HTML Editor für Textaufgaben");
         * taskViewRepository.save(htmlEditor);
         * System.out.println("TaskView with templatePath '" + htmlEditorTemplatePath +
         * "' initialized");
         * }
         */
    }

    private void initializeUnitTitles() {
        // Unit Titles mit Gewichtung für die Anzeige-Reihenfolge
        // Niedrigere weight-Werte werden zuerst angezeigt
        unitTitleService.createOrUpdate("einfuehrung-programmierung",
                "Einführung in die Programmierung",
                "Grundlagen der Programmierung und erste Schritte",
                10);

        unitTitleService.createOrUpdate("html-css",
                "HTML & CSS",
                "Webentwicklung mit HTML und CSS",
                20);

        unitTitleService.createOrUpdate("javascript-basics",
                "JavaScript Grundlagen",
                "Einführung in JavaScript und DOM-Manipulation",
                30);

        unitTitleService.createOrUpdate("datenbanken",
                "Datenbanken",
                "Grundlagen von Datenbanken und SQL",
                40);

        unitTitleService.createOrUpdate("objektorientierung",
                "Objektorientierte Programmierung",
                "Klassen, Objekte und Vererbung",
                50);

        // Weitere Unit Titles können hier hinzugefügt werden
        // Die Gewichtung bestimmt die Reihenfolge auf der Schüler-Seite
    }
}