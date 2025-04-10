package com.server;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

public class FaceRecognition {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = Imgcodecs.imread("server/images/face.webp");

        detectAndSave(image);
    }
    private static void detectAndSave(Mat image){
        MatOfRect faces = new MatOfRect();

        Mat grayFrame = new Mat();
        Imgproc.cvtColor(image, grayFrame, Imgproc.COLOR_BGR2GRAY);

        Imgproc.equalizeHist(grayFrame, grayFrame);

        int height = grayFrame.height();
        int absoluteFaceSize = 0;
        if(Math.round(height * 0.2f) > 0){
            absoluteFaceSize = Math.round(height * 0.2f);
        }

        CascadeClassifier faceCascade = new CascadeClassifier();

        faceCascade.load("server/data/haarcascade_frontalface_alt2.xml");
        faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0| Objdetect.CASCADE_SCALE_IMAGE, new Size(absoluteFaceSize, absoluteFaceSize), new Size());

        Rect[] faceArray = faces.toArray();
        for (int i=0; i<faceArray.length; i++){
            Imgproc.rectangle(image, faceArray[i], new Scalar(0,0,255), 3);
        }

        Imgcodecs.imwrite("server/images/output.jpg", image);
        System.out.println("write success"+faceArray.length);
    }
}