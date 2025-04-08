package com.server.Model;

import java.util.List;

public class FacultySubject {

    private String name;
    private List<String> classes;

    // Constructors
    public FacultySubject() {}

    public FacultySubject(String name, List<String> classes) {
        this.name = name;
        this.classes = classes;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }
}
