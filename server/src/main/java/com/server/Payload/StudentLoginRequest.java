package com.server.Payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentLoginRequest {
    private String email;
    private String password;
    private String studentClass;
    
    public StudentLoginRequest(String email, String password, String studentClass) {
        this.email = email;
        this.password = password;
        this.studentClass = studentClass;
    }
}

