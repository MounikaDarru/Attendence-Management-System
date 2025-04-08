package com.server.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.Model.Student;
import com.server.Payload.StudentRegistrationRequest;
import com.server.Service.StudentService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/register")
    public ResponseEntity<Student> registerStudent(@RequestBody StudentRegistrationRequest request) {
        Student student = studentService.registerStudent(request);
        return new ResponseEntity<>(student, HttpStatus.CREATED);
    }

    
}
