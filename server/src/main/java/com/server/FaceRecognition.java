package com.server;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_core;

class FaceRecognition{
    public static void main(String[] args){
        Loader.load(opencv_core.class);
    }
}