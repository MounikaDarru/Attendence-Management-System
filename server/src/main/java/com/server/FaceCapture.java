package com.server;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_highgui.*;

import java.io.File;

public class FaceCapture {
    public static void main(String[] args) {
        String classId = "3_4 CSE A1";
        String studentId = "322106410085";  // change this for each person

        CascadeClassifier faceCascade = new CascadeClassifier("server/data/haarcascade_frontalface_alt2.xml");

        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.out.println("❌ Cannot open camera");
            return;
        }

        int imgCount = 0;
        int totalImages = 20;
        String outputDir = "server/images/" + classId + "/" + studentId;
        new File(outputDir).mkdirs();

        Mat frame = new Mat();

        while (imgCount < totalImages) {
            capture.read(frame);
            if (!frame.empty()) {
                Mat gray = new Mat();
                cvtColor(frame, gray, COLOR_BGR2GRAY);
                equalizeHist(gray, gray);

                RectVector faces = new RectVector();
                faceCascade.detectMultiScale(gray, faces);

                for (int i = 0; i < faces.size(); i++) {
                    Rect faceRect = faces.get(i);
                    rectangle(frame, faceRect, new Scalar(0, 255, 0, 0));
                    Mat faceROI = new Mat(gray, faceRect);

                    String filename = outputDir + "/" + (imgCount + 1) + ".jpg";
                    imwrite(filename, faceROI);
                    imgCount++;

                    System.out.println("📸 Saved: " + filename);
                    if (imgCount >= totalImages) break;
                }

                imshow("Capturing Faces - " + imgCount + "/" + totalImages, frame);
                if (waitKey(100) == 27) break;  // ESC key
            }
        }

        capture.release();
        destroyAllWindows();
        System.out.println("✅ Face data captured for " + studentId);
    }
}
