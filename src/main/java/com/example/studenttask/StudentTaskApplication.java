package com.example.studenttask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class StudentTaskApplication {

    public static void main(String[] args) {
        // Set default timezone to Europe/Berlin (CET/CEST)
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
        SpringApplication.run(StudentTaskApplication.class, args);
    }
}