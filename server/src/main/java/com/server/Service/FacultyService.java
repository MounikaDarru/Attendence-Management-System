package com.server.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import com.server.Model.Faculty;
import com.server.Model.FacultySubject;

@Service
public class FacultyService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void registerFaculty(Faculty faculty) {
        mongoTemplate.save(faculty, "faculties");
    }

    public Faculty login(String facultyId, String password) {
        Query query = new Query();
        query.addCriteria(Criteria.where("facultyId").is(facultyId).and("password").is(password));
    
        Faculty faculty = mongoTemplate.findOne(query, Faculty.class, "faculties");
    
        if (faculty == null) {
            throw new NoSuchElementException("Invalid Faculty ID or Password.");
        }
    
        return faculty;
    }
    
    

    public List<Object> getStudentsForFaculty(String facultyId) {
    // Fetch the faculty and their subjects
    Query facultyQuery = new Query(Criteria.where("facultyId").is(facultyId));
    Faculty faculty = mongoTemplate.findOne(facultyQuery, Faculty.class, "faculties");

    if (faculty == null) {
        throw new NoSuchElementException("Faculty not found with ID: " + facultyId);
    }

    // Get distinct class names from all subjects
    Set<String> classNames = faculty.getSubjects().stream()
        .flatMap(sub -> sub.getClasses().stream())
        .collect(Collectors.toSet());

    List<Object> allStudents = new ArrayList<>();

    // Fetch students from each class-specific collection
    for (String className : classNames) {
        List<Object> students = mongoTemplate.findAll(Object.class, className);
        allStudents.addAll(students);
    }

    return allStudents;
}
    
    
}


