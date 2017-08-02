#include "native-lib.h"

long start = 0;

JNIEXPORT jintArray
JNICALL
Java_com_wzjing_face_opencvcamera_CameraView_nativeProcess(JNIEnv *env, jobject instance, jint row,
                                                           jint col, int count, jbyteArray data_) {
    double* values = (double*)env->GetPrimitiveArrayCritical(data_, 0);

    auto frame = Mat((int) (w * 1.5), h, CV_16UC3);

    env->ReleasePrimitiveArrayCritical(data_, values, 0);
}

void put(Mat* frame, int row, int col, int count, double* values) {

}

void detectFace(Mat *frame) {
    start = clock();
    LOGI(TAG, "Frame Start:---------------------------------------------");
    if (!loaded) {
        loaded = classifier.load("/storage/emulated/0/classifier.xml");
        LOGI(TAG, "Load: %.2f ms", (clock() - start) / 1000.0);
    }
    rotate(*frame, *frame, ROTATE_90_COUNTERCLOCKWISE);
    LOGI(TAG, "Pre rotated: %.2f ms", (clock() - start) / 1000.0);
    detectAndDraw(*frame, classifier, false);
    rotate(*frame, *frame, ROTATE_90_COUNTERCLOCKWISE);
    LOGI(TAG, "End: %.2f ms", (clock() - start) / 1000.0);
}

void detectAndDraw(Mat &frame, CascadeClassifier &cascade, bool tryflip) {
    LOGI(TAG, "Detection Start: %.2f ms", (clock() - start) / 1000.0);
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
    LOGI(TAG, "Detection Pre-gray: %.2f ms", (clock() - start) / 1000.0);

    double fx = 180.0/frame.cols;
    int minSize = (int) (180*fx);
    resize(gray, small_gray, Size(), fx, fx, INTER_LINEAR);
    LOGI(TAG, "Detection Pre-resize: %.2f ms", (clock() - start) / 1000.0);
    equalizeHist(small_gray, small_gray);
    LOGI(TAG, "Detection Pre-equal: %.2f ms", (clock() - start) / 1000.0);

    LOGI(TAG, "Detection Processing: %.2f ms", (clock() - start) / 1000.0);
    time = (double) getTickCount();
    cascade.detectMultiScale(small_gray,
                             faces,
                             1.5,
                             3,
                             0|CASCADE_SCALE_IMAGE,//|CASCADE_FIND_BIGGEST_OBJECT|CASCADE_DO_ROUGH_SEARCH
                             Size(minSize, minSize),
                             Size((int) (small_gray.cols * 0.6), (int) (small_gray.rows * 0.6)));

    time = (double) getTickCount() - time;
    LOGI(TAG, "detection time = %g ms\n", time * 1000 / getTickFrequency());

    LOGI(TAG, "Detection Draw: %.2f ms", (clock() - start) / 1000.0);
    for (size_t i = 0; i < faces.size(); i++) {
        Scalar color = colors[i % 8];

        int tx = (int) (faces[i].x / fx);
        int ty = (int) (faces[i].y / fx);
        int w = (int) (faces[i].width / fx);
        int h = (int) (faces[i].height / fx);
        rectangle(frame, Rect(tx, ty, w, h), color, 3, 8, 0);
    }
    LOGI(TAG, "Detection End: %.2f ms", (clock() - start) / 1000.0);
}