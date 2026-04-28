package com.example.studenttask.service;

import com.example.studenttask.exception.TeacherAuthenticationRequiredException;
import com.example.studenttask.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeacherIdentityService {

    @Autowired
    private UserService userService;

    public User requireTeacher(String openIdSubject) {
        return userService.findByOpenIdSubject(openIdSubject)
            .orElseThrow(() -> new TeacherAuthenticationRequiredException("Benutzer nicht gefunden"));
    }

    public User requireTeacher(User teacher) {
        if (teacher == null) {
            throw new TeacherAuthenticationRequiredException("Benutzer nicht gefunden");
        }
        return teacher;
    }
}
