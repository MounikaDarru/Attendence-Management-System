//package com.server;
//
//import org.bytedeco.opencv.global.opencv_imgproc;
//import org.bytedeco.opencv.global.opencv_imgcodecs;
//import org.bytedeco.opencv.global.opencv_highgui;
//import org.bytedeco.opencv.opencv_core.*;
//import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
//import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
//import org.bytedeco.opencv.opencv_videoio.VideoCapture;
//
//import java.io.*;
//import java.util.*;
//
//public class FaceRecognizer {
//
//    public static void main(String[] args) {
//        String modelPath = "server/data/faceRecognizerModel.xml";
//        String labelMapPath = "server/data/label_map.txt";
//        String cascadePath = "server/data/haarcascade_frontalface_alt2.xml";
//
//        Map<Integer, String> labelToStudentId = loadLabelMap(labelMapPath);
//        if (labelToStudentId == null) return;
//
//        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
//        recognizer.read(modelPath);
//
//        CascadeClassifier faceCascade = new CascadeClassifier(cascadePath);
//
//        VideoCapture capture = new VideoCapture(0);
//        if (!capture.isOpened()) {
//            System.out.println("❌ Cannot open webcam.");
//            return;
//        }
//
//        Mat frame = new Mat();
//        Mat gray = new Mat();
//        RectVector faces = new RectVector();
//
//        System.out.println("🎥 Starting live recognition... Press ESC to exit.");
//
//        while (true) {
//            capture.read(frame);
//            if (frame.empty()) continue;
//
//            // Convert to grayscale
//            opencv_imgproc.cvtColor(frame, gray, opencv_imgproc.COLOR_BGR2GRAY);
//            opencv_imgproc.equalizeHist(gray, gray);
//
//            // Detect faces
//            faceCascade.detectMultiScale(gray, faces);
//
//            for (int i = 0; i < faces.size(); i++) {
//                Rect face = faces.get(i);
//                Mat faceROI = new Mat(gray, face);
//
//                int[] predictedLabel = new int[1];
//                double[] confidence = new double[1];
//                recognizer.predict(faceROI, predictedLabel, confidence);
//
//                String labelText;
//                double threshold = 40.0;
//                int label = predictedLabel[0];
//                double conf = confidence[0];
//
//                if (labelToStudentId.containsKey(label) && conf <= threshold) {
//                    labelText = "ID: " + labelToStudentId.get(label) + " (" + String.format("%.1f", conf) + ")";
//                } else {
//                    labelText = "Unknown (" + String.format("%.1f", conf) + ")";
//                }
//
//                // Draw bounding box & label
//                opencv_imgproc.rectangle(frame, face, new Scalar(0, 255, 0, 0));
//                opencv_imgproc.putText(frame, labelText, new Point(face.x(), face.y() - 10),
//                        opencv_imgproc.FONT_HERSHEY_SIMPLEX, 0.6, new Scalar(0, 255, 0, 0));
//            }
//
//            opencv_highgui.imshow("🎯 Live Face Recognition", frame);
//            if (opencv_highgui.waitKey(30) == 27) break; // ESC key to exit
//        }
//
//        capture.release();
//        opencv_highgui.destroyAllWindows();
//        System.out.println("✅ Recognition stopped.");
//    }
//
//    private static Map<Integer, String> loadLabelMap(String path) {
//        Map<Integer, String> labelToStudentId = new HashMap<>();
//        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] parts = line.split("=");
//                if (parts.length == 2) {
//                    String studentId = parts[0];
//                    int label = Integer.parseInt(parts[1]);
//                    labelToStudentId.put(label, studentId);
//                }
//            }
//            System.out.println("✅ Label map loaded.");
//        } catch (IOException e) {
//            System.out.println("❌ Failed to load label map: " + e.getMessage());
//            return null;
//        }
//        return labelToStudentId;
//    }
//}


package com.server.FaceRecognition;

import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import java.io.*;
import java.util.*;

public class FaceRecognizer {

    private static final String MODEL_PATH = "server/data/faceRecognizerModel.xml";
    private static final String LABEL_MAP_PATH = "server/data/label_map.txt";
    private static final String CASCADE_PATH = "server/data/haarcascade_frontalface_alt2.xml";
    private static final double THRESHOLD = 50.0;
    private static final long TIMEOUT = 30_000; // 60 seconds

    public static String recognizeFace() {
        Map<Integer, String> labelToStudentId = loadLabelMap(LABEL_MAP_PATH);
        if (labelToStudentId == null || labelToStudentId.isEmpty()) {
            System.err.println("❌ Label map is empty or failed to load.");
            return "Label map is empty or failed to load";
        }

        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
        recognizer.read(MODEL_PATH);

        CascadeClassifier faceCascade = new CascadeClassifier(CASCADE_PATH);
        if (faceCascade.empty()) {
            System.err.println("❌ Failed to load Haar Cascade.");
            return "Failed to load Haar Cascade";
        }

        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.err.println("❌ Cannot open webcam.");
            return "Cannot open webcam";
        }

        System.out.println("⏳ Scanning for face match...");

        Mat frame = new Mat();
        Mat gray = new Mat();
        RectVector faces = new RectVector();

        long startTime = System.currentTimeMillis();
        String result = "Unknown";

        String studenId = null;
        while ((System.currentTimeMillis() - startTime) < TIMEOUT) {
            capture.read(frame);
            if (frame.empty()) continue;

            opencv_imgproc.cvtColor(frame, gray, opencv_imgproc.COLOR_BGR2GRAY);
            opencv_imgproc.equalizeHist(gray, gray);
            faceCascade.detectMultiScale(gray, faces);

            for (int i = 0; i < faces.size(); i++) {
                Rect face = faces.get(i);
                Mat faceROI = new Mat(gray, face);

                int[] predictedLabel = new int[1];
                double[] confidence = new double[1];
                recognizer.predict(faceROI, predictedLabel, confidence);

                int label = predictedLabel[0];
                double conf = confidence[0];

                if (labelToStudentId.containsKey(label) && conf <= THRESHOLD) {
                    result = "ID: " + labelToStudentId.get(label) + " (Confidence: " + String.format("%.1f", conf) + ")";
                    studenId = labelToStudentId.get(label);
                    break;
                }
            }

            opencv_highgui.imshow("🔍 Recognizing...", frame);
            if (opencv_highgui.waitKey(30) == 27 || !result.equals("Unknown")) break;
        }

        capture.release();
        opencv_highgui.destroyAllWindows();

        System.out.println("🧾 Result: " + result);
        return studenId;
    }

    private static Map<Integer, String> loadLabelMap(String path) {
    Map<Integer, String> labelToStudentId = new HashMap<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("=");
            if (parts.length == 2) {
                String studentId = parts[0].trim();
                int label = Integer.parseInt(parts[1].trim());
                labelToStudentId.put(label, studentId);
            }
        }
        System.out.println("✅ Label map loaded with " + labelToStudentId.size() + " entries.");
    } catch (IOException e) {
        System.err.println("❌ Failed to load label map: " + e.getMessage());
        return null;
    }
    return labelToStudentId;
}

}

