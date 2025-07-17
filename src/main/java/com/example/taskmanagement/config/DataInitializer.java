
package com.example.taskmanagement.config;

import com.example.taskmanagement.model.Group;
import com.example.taskmanagement.model.Role;
import com.example.taskmanagement.repository.GroupRepository;
import com.example.taskmanagement.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("=== DataInitializer starting ===");
        
        // Initialize roles only if they don't exist
        if (roleRepository.count() == 0) {
            System.out.println("Creating default roles...");
            
            Role studentRole = new Role();
            studentRole.setName("STUDENT");
            studentRole.setDescription("Schüler-Rolle für Aufgabenbearbeitung");
            roleRepository.save(studentRole);
            
            Role teacherRole = new Role();
            teacherRole.setName("TEACHER");
            teacherRole.setDescription("Lehrer-Rolle für Aufgabenverwaltung");
            roleRepository.save(teacherRole);
            
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Administrator-Rolle für Systemverwaltung");
            roleRepository.save(adminRole);
            
            System.out.println("Default roles created successfully");
        } else {
            System.out.println("Roles already exist, skipping creation");
        }
        
        // Initialize groups only if they don't exist
        if (groupRepository.count() == 0) {
            System.out.println("Creating default groups...");
            
            Group group1 = new Group();
            group1.setName("Klasse 10A");
            group1.setDescription("Mathematik Klasse 10A");
            groupRepository.save(group1);
            
            Group group2 = new Group();
            group2.setName("Klasse 10B");
            group2.setDescription("Mathematik Klasse 10B");
            groupRepository.save(group2);
            
            Group group3 = new Group();
            group3.setName("Informatik AG");
            group3.setDescription("Informatik Arbeitsgemeinschaft");
            groupRepository.save(group3);
            
            System.out.println("Default groups created successfully");
        } else {
            System.out.println("Groups already exist, skipping creation");
        }
        
        System.out.println("=== DataInitializer finished ===");
    }
}
