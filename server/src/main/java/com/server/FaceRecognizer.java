package com.server;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;

import java.io.*;
import java.util.*;

public class FaceRecognizer {

    public static void main(String[] args) {
        String modelPath = "server/data/faceRecognizerModel.xml";
        String labelMapPath = "server/data/label_map.txt";
        String testImagePath = "server/test/test_image.jpg"; // change this path to your test image

        Map<Integer, String> labelToStudentId = loadLabelMap(labelMapPath);
        if (labelToStudentId == null) return;

        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
        recognizer.read(modelPath);

        Mat testImage = opencv_imgcodecs.imread(testImagePath, opencv_imgcodecs.IMREAD_GRAYSCALE);
        if (testImage.empty()) {
            System.out.println("❌ Test image not found or empty: " + testImagePath);
            return;
        }

        int[] predictedLabel = new int[1];
        double[] confidence = new double[1];
        recognizer.predict(testImage, predictedLabel, confidence);

        int label = predictedLabel[0];
        String studentId = labelToStudentId.get(label);

        if (studentId != null) {
            System.out.println("✅ Prediction:");
            System.out.println("   Label: " + label);
            System.out.println("   Student ID: " + studentId);
            System.out.println("   Confidence: " + confidence[0]);
        } else {
            System.out.println("❌ Label not recognized in mapping: " + label);
        }
    }

    private static Map<Integer, String> loadLabelMap(String path) {
        Map<Integer, String> labelToStudentId = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String studentId = parts[0];
                    int label = Integer.parseInt(parts[1]);
                    labelToStudentId.put(label, studentId);
                }
            }
            System.out.println("✅ Label map loaded.");
        } catch (IOException e) {
            System.out.println("❌ Failed to load label map: " + e.getMessage());
            return null;
        }
        return labelToStudentId;
    }
}
