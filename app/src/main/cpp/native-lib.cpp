#include "native-lib.h"

long start = 0;

JNIEXPORT void JNICALL
Java_com_wzjing_face_opencvcamera_OpenCVCameraActivity_rotateFrame(JNIEnv *env, jobject /* this */,
                                                                   jlong frame, jfloat degree) {

    start = clock();
    LOGI(ATAG, "Frame Start:---------------------------------------------");
    Mat *src = (Mat *) frame;
    if (!loaded) {
        loaded = classifier.load("/storage/emulated/0/classifier.xml");
        LOGI(ATAG, "Load: %.2f", (clock() - start) / 1000.0);
    }
    transpose(*src, *src);
    flip(*src, *src, 1);
    LOGI(ATAG, "Pre rotated: %.2f", (clock() - start) / 1000.0);
    detectAndDraw(*src, classifier, false);
    transpose(*src, *src);
    flip(*src, *src, 0);
    LOGI(ATAG, "End: %.2f", (clock() - start) / 1000.0);
}

void detectAndDraw(Mat &frame, CascadeClassifier &cascade, bool tryflip) {
    LOGI(ATAG, "Detection Start: %.2f", (clock() - start) / 1000.0);
    double time = 0;
    vector<Rect> faces, faces2;
    const static Scalar colors[] =
            {
                    Scalar(255, 0, 0),
                    Scalar(255, 128, 0),
                    Scalar(255, 255, 0),
                    Scalar(0, 255, 0),
                    Scalar(0, 128, 255),
                    Scalar(0, 255, 255),
                    Scalar(0, 0, 255),
                    Scalar(255, 0, 255)
            };
    Mat gray, small_gray;

    cvtColor(frame, gray, COLOR_BGR2GRAY);
    LOGI(ATAG, "Detection Pre-gray: %.2f", (clock() - start) / 1000.0);

    double fx = 0.3;
    resize(gray, small_gray, Size(), fx, fx, INTER_LINEAR);
    LOGI(ATAG, "Detection Pre-resize: %.2f", (clock() - start) / 1000.0);
    equalizeHist(small_gray, small_gray);
    LOGI(ATAG, "Detection Pre-equal: %.2f", (clock() - start) / 1000.0);

    LOGI(ATAG, "Detection Main: %.2f", (clock() - start) / 1000.0);
    time = (double) getTickCount();
    cascade.detectMultiScale(small_gray,
                             faces,
                             1.1,
                             3,
                             0 |
                             CASCADE_SCALE_IMAGE,//|CASCADE_FIND_BIGGEST_OBJECT|CASCADE_DO_ROUGH_SEARCH
                             Size(50, 50));

    time = (double) getTickCount() - time;
    LOGI(ATAG, "detection time = %g ms\n", time * 1000 / getTickFrequency());

    LOGI(ATAG, "Detection Draw: %.2f", (clock() - start) / 1000.0);
    for (size_t i = 0; i < faces.size(); i++) {
        Scalar color = colors[i % 8];

        int tx = (int) (faces[i].x / fx);
        int ty = (int) (faces[i].y / fx);
        int w = (int) (faces[i].width / fx);
        int h = (int) (faces[i].height / fx);
        rectangle(frame, Rect(tx, ty, w, h), color, 3, 8, 0);
    }
    LOGI(ATAG, "Detection End: %.2f", (clock() - start) / 1000.0);
}