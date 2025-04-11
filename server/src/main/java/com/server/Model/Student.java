package com.server.Model;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Student {
    private String rollNo;
    private String name;
    private String email;
    private String password;
    private String studentClass;
    private List<Subject> subjects;

}
