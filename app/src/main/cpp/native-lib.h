#ifndef FACE_NATIVE_LIB_H
#define FACE_NATIVE_LIB_H

#include <string>
#include <jni.h>
#include <iostream>
#include <bitset>
#include <vector>
#include <android/log.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/objdetect.hpp>

#define ATAG "opencv_native"
#define LOGI(TAG, format, ...) __android_log_print(ANDROID_LOG_INFO, TAG, format, ## __VA_ARGS__)

using namespace cv;

extern "C" {
JNIEXPORT void JNICALL
Java_com_wzjing_face_opencvcamera_OpenCVCameraActivity_rotateFrame(JNIEnv *env, jobject /* this */, jlong frame, jfloat degree);
};
void detectAndDraw( Mat& frame, CascadeClassifier& cascade, bool tryflip );

#endif //FACE_NATIVE_LIB_H
