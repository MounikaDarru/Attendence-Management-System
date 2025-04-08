package com.server.Payload;

import lombok.Data;

@Data
public class StudentRegistrationRequest {
    private String rollNo;
    private String name;
    private String email;
    private String password;
    private String studentClass;

    public StudentRegistrationRequest(String rollNo, String name, String email, String password, String studentClass) {
        this.rollNo = rollNo;
        this.name = name;
        this.email = email;
        this.password = password;
        this.studentClass = studentClass;
    }
    
}
