package com.server.Controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.Model.Faculty;
import com.server.Payload.FacultyLoginRequest;
import com.server.Service.FacultyService;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @PostMapping("/register")
    public ResponseEntity<?> registerFaculty(@RequestBody Faculty faculty) {
        try {
            facultyService.registerFaculty(faculty);
            return ResponseEntity.status(HttpStatus.CREATED).body("Faculty registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginFaculty(@RequestBody FacultyLoginRequest loginRequest) {
        try {
            Faculty faculty = facultyService.login(loginRequest.getFacultyId(), loginRequest.getPassword());
            return ResponseEntity.ok(faculty);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/{facultyId}/students")
    public ResponseEntity<?> getStudentsForFaculty(@PathVariable String facultyId) {
        try {
            List<Object> students = facultyService.getStudentsForFaculty(facultyId);
            return ResponseEntity.ok(students);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
    
}


