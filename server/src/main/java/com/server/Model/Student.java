package com.server.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private String rollNo;
    private String name;
    private String email;
    private String password;
    private String studentClass;
    private List<Subject> subjects;
}
