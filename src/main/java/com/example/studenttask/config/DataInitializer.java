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
import com.example.studenttask.service.TaskViewService;
import com.example.studenttask.service.TaskStatusService;
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
        String simpleTextTemplatePath = "taskviews/simple-text";

        if (taskViewRepository.findByTemplatePath(simpleTextTemplatePath) == null) {
            TaskView simpleText = new TaskView("Einfacher Texteditor", simpleTextTemplatePath);
            simpleText.setDescription("Einfaches Textfeld für Text-Abgaben");
            taskViewRepository.save(simpleText);
            System.out.println("TaskView with templatePath '" + simpleTextTemplatePath + "' initialized");
        }

        // Hier können weitere TaskViews hinzugefügt werden
        // Beispiel für weitere TaskViews:
        /*
        String htmlEditorTemplatePath = "taskviews/html-editor";
        if (taskViewRepository.findByTemplatePath(htmlEditorTemplatePath) == null) {
            TaskView htmlEditor = new TaskView("HTML Editor", htmlEditorTemplatePath);
            htmlEditor.setDescription("Rich-Text HTML Editor für Textaufgaben");
            taskViewRepository.save(htmlEditor);
            System.out.println("TaskView with templatePath '" + htmlEditorTemplatePath + "' initialized");
        }
        */
    }

    private void initializeUnitTitles() {
        // Beispiel Unit Titles - können hier einfach erweitert werden
        unitTitleService.createIfNotExists("einfuehrung-programmierung",
            "Einführung in die Programmierung",
            "Grundlagen der Programmierung und erste Schritte");

        unitTitleService.createIfNotExists("html-css",
            "HTML & CSS",
            "Webentwicklung mit HTML und CSS");

        unitTitleService.createIfNotExists("javascript-basics",
            "JavaScript Grundlagen",
            "Einführung in JavaScript und DOM-Manipulation");

        unitTitleService.createIfNotExists("datenbanken",
            "Datenbanken",
            "Grundlagen von Datenbanken und SQL");

        unitTitleService.createIfNotExists("objektorientierung",
            "Objektorientierte Programmierung",
            "Klassen, Objekte und Vererbung");
    }
}
`