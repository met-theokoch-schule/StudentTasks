package com.example.studenttask.controller;

import com.example.studenttask.dto.GroupOverviewDto;
import com.example.studenttask.dto.GroupStatisticsDto;
import com.example.studenttask.dto.StudentTaskMatrixDto;
import com.example.studenttask.model.Group;
import com.example.studenttask.model.User;
import com.example.studenttask.service.GroupQueryService;
import com.example.studenttask.service.GroupService;
import com.example.studenttask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/teacher/groups")
@PreAuthorize("@userService.hasTeacherRole(authentication.name)")
public class TeacherGroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupQueryService groupQueryService;

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
        List<GroupOverviewDto> groups = groupQueryService.getGroupsWithActiveTasksByTeacher(teacher);

        model.addAttribute("groups", groups);
        model.addAttribute("teacher", teacher);

        return "teacher/groups-list";
    }

    /**
     * Zeigt Details einer Gruppe mit allen Schülern und ihren Aufgaben
     */
    @GetMapping("/{groupId}")
    @PreAuthorize("@userService.hasTeacherRole(authentication.name)")
    public String showGroupDetail(@PathVariable Long groupId, Model model, Principal principal) {
        User teacher = userService.findByOpenIdSubject(principal.getName())
            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Lade Gruppe
        Group group = groupService.findById(groupId);
        if (group == null) {
            throw new RuntimeException("Gruppe nicht gefunden");
        }

        // Lade Statistiken für die Gruppe
        GroupStatisticsDto statistics = groupQueryService.getGroupStatistics(group, teacher);

        // Matrix-Daten erstellen
        StudentTaskMatrixDto matrix = groupQueryService.getStudentTaskMatrix(group);

        model.addAttribute("group", group);
        model.addAttribute("statistics", statistics);
        model.addAttribute("matrix", matrix);

        return "teacher/group-detail";
    }
}
