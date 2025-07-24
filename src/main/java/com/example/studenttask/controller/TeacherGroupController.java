package com.example.studenttask.controller;

import com.example.studenttask.model.*;
import com.example.studenttask.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher/groups")
public class TeacherGroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    /**
     * Zeigt alle Gruppen mit aktiven Aufgaben
     */
    @GetMapping
    public String listGroups(Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Lade Gruppen mit aktiven Aufgaben des Lehrers
        List<GroupService.GroupInfo> groups = groupService.getGroupsWithActiveTasksByTeacher(teacher);

        model.addAttribute("groups", groups);
        model.addAttribute("teacher", teacher);

        return "teacher/groups-list";
    }

    /**
     * Zeigt Details einer Gruppe mit allen Schülern und ihren Aufgaben
     */
    @GetMapping("/{groupId}")
    public String groupDetail(@PathVariable Long groupId, Model model, Principal principal, HttpServletRequest request) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Lade Gruppe
        Group group = groupService.findById(groupId);
        if (group == null) {
            throw new RuntimeException("Gruppe nicht gefunden");
        }

        // Lade Statistiken für die Gruppe
        GroupService.GroupStatistics statistics = groupService.getGroupStatistics(group, teacher);

        // Lade Matrix-Daten für die Gruppe
        Map<String, Object> matrix = groupService.getStudentTaskMatrix(group, teacher);

        model.addAttribute("group", group);
        model.addAttribute("statistics", statistics);
        model.addAttribute("matrix", matrix);
        model.addAttribute("teacher", teacher);

        return "teacher/group-detail";
    }
}