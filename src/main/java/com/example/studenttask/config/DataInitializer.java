package com.example.studenttask.config;

import com.example.studenttask.model.Role;
import com.example.studenttask.model.TaskStatus;
import com.example.studenttask.model.TaskView;
import com.example.studenttask.model.Category;
import com.example.studenttask.repository.RoleRepository;
import com.example.studenttask.repository.TaskStatusRepository;
import com.example.studenttask.repository.TaskViewRepository;
import com.example.studenttask.repository.CategoryRepository;
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
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeRoles();
        initializeTaskStatuses();
        initializeTaskViews();
        initializeCategories();
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
    
    private void initializeCategories() {
        // Initialize Categories if they don't exist
        createCategoryIfNotExists("Mathematik", "Mathematische Aufgaben und Übungen");
        createCategoryIfNotExists("Deutsch", "Deutsche Sprache und Literatur");
        createCategoryIfNotExists("Englisch", "Englische Sprache und Literatur");
        createCategoryIfNotExists("Informatik", "Programmierung und Informatik");
        createCategoryIfNotExists("Naturwissenschaften", "Physik, Chemie, Biologie");
        createCategoryIfNotExists("Geschichte", "Geschichtliche Themen und Ereignisse");
        createCategoryIfNotExists("Geographie", "Erdkunde und geografische Themen");
        createCategoryIfNotExists("Kunst", "Künstlerische und kreative Aufgaben");
        createCategoryIfNotExists("Sport", "Sportliche Aktivitäten und Theorie");
        createCategoryIfNotExists("Allgemein", "Allgemeine und fächerübergreifende Aufgaben");
    }
    
    private void createCategoryIfNotExists(String name, String description) {
        if (categoryRepository.findByNameIgnoreCase(name).isEmpty()) {
            Category category = new Category(name, description);
            categoryRepository.save(category);
            System.out.println("Category '" + name + "' initialized");
        }
    }
}