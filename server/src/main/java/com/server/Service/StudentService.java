package com.server.Service;

import com.server.Model.Student;
import com.server.Model.Subject;
import com.server.Model.ClassSection;
import com.server.Payload.StudentRegistrationRequest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Student registerStudent(StudentRegistrationRequest request) {
        // Fetch the class section
        Query query = new Query();
        String className = request.getStudentClass().trim();
        query.addCriteria(Criteria.where("className").is(className));
        ClassSection classSection = mongoTemplate.findOne(query, ClassSection.class, "class_subjects");
    
        if (classSection == null) {
            throw new RuntimeException("Class not found: " + request.getStudentClass());
        }
    
        List<Subject> subjects = classSection.getSubjects();
    
        Student student = new Student();
        student.setRollNo(request.getRollNo());
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setStudentClass(request.getStudentClass());
        student.setSubjects(subjects);
    
        // Save student to class-named collection
        Student saved = mongoTemplate.save(student, request.getStudentClass());
        System.out.println("Student registered and saved in class: " + request.getStudentClass());
        return saved;
    }
    
    public Student login(String email, String password, String studentClass) {
        // Sanitize the studentClass to match MongoDB collection format
        String collectionName = studentClass;
    
        // Query to find the student based on email in the respective class collection
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
    
        // Retrieve the student from the specified class collection
        Student student = mongoTemplate.findOne(query, Student.class, collectionName);
    
        // Log student details if found
        if (student != null) {
            System.out.println("Student found: " + student.getEmail());
            System.out.println("Stored password (encoded): " + student.getPassword());
        } else {
            System.out.println("No student found with email: " + email);
        }
    
        // Validate the password using passwordEncoder
        if (student == null || !passwordEncoder.matches(password, student.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
    
        return student;
    }
    
}
