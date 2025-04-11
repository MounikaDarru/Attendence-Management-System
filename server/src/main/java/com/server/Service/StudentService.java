package com.server.Service;

import com.server.Model.Student;
import com.server.Model.Subject;
import com.server.Model.ClassSection;
import com.server.Payload.StudentRegistrationRequest;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

//    public boolean markAttendance(String studentId) {
//        List<String> collectionNames = mongoTemplate.getCollectionNames().stream().toList();
//        Query studentQuery = new Query(Criteria.where("rollNo").is(studentId));
//
//        for (String collectionName : collectionNames) {
//            Map studentMap = mongoTemplate.findOne(studentQuery, Map.class, collectionName);
//
//            if (studentMap != null && studentMap.containsKey("studentClass")) {
//                String className = (String) studentMap.get("studentClass");
//
//                List<Map<String, Object>> subjects = (List<Map<String, Object>>) studentMap.get("subjects");
//
//                if (subjects != null) {
//                    for (Map<String, Object> subject : subjects) {
//                        List<Map<String, Object>> months = (List<Map<String, Object>>) subject.get("attendence");
//
//                        if (months != null) {
//                            for (Map<String, Object> month : months) {
//                                List<Map<String, Object>> dateEntries = (List<Map<String, Object>>) month.get("attendence");
//
//                                if (dateEntries != null) {
//                                    for (Map<String, Object> dateEntry : dateEntries) {
//                                        dateEntry.put("present", true);
//                                    }
//                                    // Optional: update totalPresent
//                                    month.put("totalPresent", dateEntries.size());
//                                }
//                            }
//                        }
//                    }
//
//                    // Save updated subjects array
//                    Update update = new Update().set("subjects", subjects);
//                    mongoTemplate.updateFirst(studentQuery, update, collectionName);
//
//                    System.out.println("✅ Attendance marked for all subjects of student: " + studentId + " in class: " + className);
//                    return true;
//                }
//            }
//        }
//
//        System.out.println("❌ Student not found in any collection for ID: " + studentId);
//        return false;
//    }

    public boolean markAttendance(String studentId) {
        List<String> collectionNames = mongoTemplate.getCollectionNames().stream().toList();
        Query studentQuery = new Query(Criteria.where("rollNo").is(studentId));

        // Format today's date as dd/MM/yyyy and extract month name (e.g., "April")
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
        LocalDate today = LocalDate.now();

        String currentDate = today.format(dateFormatter); // e.g., "11/04/2025"
        String currentMonth = today.format(monthFormatter); // e.g., "April"

        for (String collectionName : collectionNames) {
            Map studentMap = mongoTemplate.findOne(studentQuery, Map.class, collectionName);

            if (studentMap != null && studentMap.containsKey("studentClass")) {
                String className = (String) studentMap.get("studentClass");

                List<Map<String, Object>> subjects = (List<Map<String, Object>>) studentMap.get("subjects");

                if (subjects != null) {
                    boolean updated = false;

                    for (Map<String, Object> subject : subjects) {
                        List<Map<String, Object>> months = (List<Map<String, Object>>) subject.get("attendence");

                        if (months != null) {
                            for (Map<String, Object> month : months) {
                                String monthName = (String) month.get("month");

                                if (monthName.equalsIgnoreCase(currentMonth)) {
                                    List<Map<String, Object>> dateEntries = (List<Map<String, Object>>) month.get("attendence");

                                    if (dateEntries != null) {
                                        for (Map<String, Object> dateEntry : dateEntries) {
                                            String date = (String) dateEntry.get("date");

                                            if (currentDate.equals(date)) {
                                                dateEntry.put("present", true);
                                                updated = true;
                                                break; // Found and updated the entry, no need to continue
                                            }
                                        }

                                        // Recalculate totalPresent only if updated
                                        if (updated) {
                                            int totalPresent = (int) dateEntries.stream()
                                                    .filter(e -> Boolean.TRUE.equals(e.get("present")))
                                                    .count();
                                            month.put("totalPresent", totalPresent);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (updated) {
                        // Save the updated subjects array
                        Update update = new Update().set("subjects", subjects);
                        mongoTemplate.updateFirst(studentQuery, update, collectionName);

                        System.out.println("✅ Attendance marked for " + studentId + " on " + currentDate);
                        return true;
                    }
                }
            }
        }

        System.out.println("❌ Student not found or no matching attendance entry for today: " + studentId);
        return false;
    }

}
