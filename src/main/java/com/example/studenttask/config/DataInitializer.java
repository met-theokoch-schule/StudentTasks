
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

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskViewRepository taskViewRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeRoles();
        initializeTaskStatuses();
        initializeTaskViews();
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
        if (taskViewRepository.count() == 0) {
            TaskView simpleText = new TaskView("simple-text", "Einfacher Texteditor", "taskviews/simple-text");
            simpleText.setDescription("Einfaches Textfeld für Text-Abgaben");
            taskViewRepository.save(simpleText);

            TaskView htmlEditor = new TaskView("html-editor", "HTML Editor", "taskviews/html-editor");
            htmlEditor.setDescription("Rich-Text HTML Editor für Textaufgaben");
            taskViewRepository.save(htmlEditor);

            TaskView mathExercise = new TaskView("math-exercise", "Mathematik Übung", "taskviews/math-exercise");
            mathExercise.setDescription("Interaktive Mathematik-Aufgaben mit LaTeX");
            taskViewRepository.save(mathExercise);

            TaskView codeEditor = new TaskView("code-editor", "Code Editor", "taskviews/code-editor");
            codeEditor.setDescription("Syntax-highlightender Code-Editor");
            taskViewRepository.save(codeEditor);

            System.out.println("Default task views initialized");
        }
    }
}
