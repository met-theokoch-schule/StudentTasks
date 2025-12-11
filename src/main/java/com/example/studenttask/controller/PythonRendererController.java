package com.example.studenttask.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/renderer")
public class PythonRendererController {

    @GetMapping("/python")
    public String pythonRenderer() {
        return "python-renderer";
    }
}
