package com.server.FaceRecognition;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;

import java.io.*;
import java.util.*;

import static org.bytedeco.opencv.global.opencv_face.*;

public class FaceTrainer {

    public static void faceTrainer(){
        String datasetPath = "server/images"; // base directory
        String modelPath = "server/data/faceRecognizerModel.xml"; // save model
        String labelMapPath = "server/data/label_map.txt"; // save label mapping

        List<Mat> images = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
        Map<String, Integer> labelMap = new HashMap<>();

        loadTrainingImages(datasetPath, images, labels, labelMap);
        saveLabelMap(labelMap, labelMapPath);
        trainAndSaveModel(images, labels, modelPath);
    }

    private static void loadTrainingImages(String rootPath, List<Mat> images, List<Integer> labels, Map<String, Integer> labelMap) {
        File rootDir = new File(rootPath);
        if (!rootDir.exists()) {
            System.out.println("❌ Training directory not found.");
            return;
        }

        int currentLabel = 0;
        for (File classDir : rootDir.listFiles()) {
            if (classDir.isDirectory()) {
                for (File studentDir : classDir.listFiles()) {
                    if (studentDir.isDirectory()) {
                        String studentId = studentDir.getName();

                        // assign a unique integer label
                        int label = currentLabel++;
                        labelMap.put(studentId, label);
                        System.out.println("✅ Mapping: " + studentId + " → " + label);

                        for (File imageFile : studentDir.listFiles()) {
                            if (imageFile.getName().toLowerCase().endsWith(".jpg")) {
                                Mat img = opencv_imgcodecs.imread(imageFile.getAbsolutePath(), opencv_imgcodecs.IMREAD_GRAYSCALE);
                                if (!img.empty()) {
                                    images.add(img);
                                    labels.add(label);
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("✅ Loaded " + images.size() + " images with " + labels.size() + " labels.");
    }

    private static void saveLabelMap(Map<String, Integer> labelMap, String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (Map.Entry<String, Integer> entry : labelMap.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
            System.out.println("✅ Label map saved at: " + path);
        } catch (IOException e) {
            System.out.println("❌ Failed to save label map: " + e.getMessage());
        }
    }

    private static void trainAndSaveModel(List<Mat> images, List<Integer> labels, String modelPath) {
        if (images.isEmpty() || labels.isEmpty()) {
            System.out.println("❌ No training data available.");
            return;
        }

        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();

        MatVector imageVector = new MatVector(images.size());
        Mat labelMat = new Mat(images.size(), 1, opencv_core.CV_32SC1);
        IntPointer labelPointer = new IntPointer(images.size());

        for (int i = 0; i < images.size(); i++) {
            imageVector.put(i, images.get(i));
            labelPointer.put(i, labels.get(i));
        }

        labelMat.data().put(labelPointer);
        recognizer.train(imageVector, labelMat);
        recognizer.save(modelPath);

        System.out.println("✅ Face recognizer model trained and saved at: " + modelPath);
    }
}
