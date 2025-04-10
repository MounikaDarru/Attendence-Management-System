//Face Detection

//package com.server;
//
//import org.opencv.core.*;
//import org.opencv.videoio.VideoCapture;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.objdetect.CascadeClassifier;
//import org.opencv.highgui.HighGui;
//import org.opencv.imgcodecs.Imgcodecs;
//
//public class FaceRecognition {
//    public static void main(String[] args) {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        CascadeClassifier faceCascade = new CascadeClassifier();
//        faceCascade.load("server/data/haarcascade_frontalface_alt2.xml");
//
//        VideoCapture capture = new VideoCapture(0); // 0 = default webcam
//        if (!capture.isOpened()) {
//            System.out.println("Error: Camera not detected");
//            return;
//        }
//
//        Mat frame = new Mat();
//
//        while (true) {
//            if (capture.read(frame)) {
//                Mat gray = new Mat();
//                Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
//                Imgproc.equalizeHist(gray, gray);
//
//                MatOfRect faces = new MatOfRect();
//                faceCascade.detectMultiScale(gray, faces, 1.1, 2, 0, new Size(100, 100), new Size());
//
//                for (Rect rect : faces.toArray()) {
//                    Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);
//                }
//
//                HighGui.imshow("Face Detection - Press ESC to Exit", frame);
//                if (HighGui.waitKey(30) == 27) break; // ESC to exit
//            }
//        }
//
//        capture.release();
//        HighGui.destroyAllWindows();
//    }
//}

//Capture Face

package com.server;

import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.highgui.HighGui;

import java.io.File;

public class FaceRecognition {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String classId = "3_4 CSE A1";
        String studentId = "322106410085";  // change this for each person

        CascadeClassifier faceCascade = new CascadeClassifier();
        faceCascade.load("server/data/haarcascade_frontalface_alt2.xml");

        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.out.println("Cannot open camera");
            return;
        }

        int imgCount = 0;
        int totalImages = 20; // Capture 20 face images per student
        String outputDir = "server/images/" + classId + "/" + studentId;
        new File(outputDir).mkdirs(); // create folder if not exist

        Mat frame = new Mat();

        while (imgCount < totalImages) {
            if (capture.read(frame)) {
                Mat gray = new Mat();
                Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
                Imgproc.equalizeHist(gray, gray);

                MatOfRect faces = new MatOfRect();
                faceCascade.detectMultiScale(gray, faces, 1.1, 2, 0, new Size(100, 100), new Size());

                for (Rect rect : faces.toArray()) {
                    Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);
                    Mat faceROI = new Mat(frame, rect);

                    // Save face image
                    String filename = outputDir + "/" + (imgCount + 1) + ".jpg";
                    Imgcodecs.imwrite(filename, faceROI);
                    imgCount++;

                    System.out.println("Saved: " + filename);
                    if (imgCount >= totalImages) break;
                }

                HighGui.imshow("Capturing Faces - " + imgCount + "/" + totalImages, frame);
                if (HighGui.waitKey(100) == 27) break; // Press ESC to exit early
            }
        }

        capture.release();
        HighGui.destroyAllWindows();
        System.out.println("✅ Face data captured for " + studentId);
    }
}

