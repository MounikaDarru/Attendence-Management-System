package com.server.Controller;

import com.server.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.server.FaceRecognition.*;

@RestController
@RequestMapping("/face")
public class FaceController {

    @Autowired
    StudentService studentService;

    @PostMapping("/capture")
    public ResponseEntity<String> captureFace(
            @RequestParam String classId,
            @RequestParam String studentId
    ) {
        try {
            // Set static vars or pass args to FaceCapture
            FaceCapture.faceCapture(classId, studentId);
            return ResponseEntity.ok("✅ Face capture completed for " + studentId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error during face capture: " + e.getMessage());
        }
    }

    @PostMapping("/train")
    public ResponseEntity<String> trainModel() {
        try {
            FaceTrainer.faceTrainer();
            return ResponseEntity.ok("✅ Model training completed");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error during training: " + e.getMessage());
        }
    }

    @GetMapping("/recognize")
    public ResponseEntity<String> recognizeFace() {
        try {
            String studentId = FaceRecognizer.recognizeFace();  // It handles displaying recognition results in the console
            if (!studentId.equals("Unknown")) {
                studentService.markAttendance(studentId); // custom method in StudentService
                System.out.println("🧾 Attendance marked for student: " + studentId);
            } else {
                System.out.println("⚠️ Face not recognized.");
            }
            return ResponseEntity.ok("✅ Recognition completed. Check console for result.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error during recognition: " + e.getMessage());
        }
    }
}
