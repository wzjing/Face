#ifndef FACE_NATIVE_LIB_H
#define FACE_NATIVE_LIB_H

#include <string>
#include <jni.h>
#include <iostream>
#include <bitset>
#include <vector>
#include <android/log.h>
#include <android/bitmap.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/objdetect.hpp>

#define TAG "opencv_native"
#define LOGI(tag, format, ...) __android_log_print(ANDROID_LOG_INFO, tag, format, ## __VA_ARGS__)
#define LOGD(tag, format, ...) __android_log_print(ANDROID_LOG_DEBUG, tag, format, ## __VA_ARGS__)
#define LOGE(tag, format, ...) __android_log_print(ANDROID_LOG_ERROR, tag, format, ## __VA_ARGS__)

using namespace cv;
using namespace std;

CascadeClassifier classifier;
bool loaded = false;

extern "C"
JNIEXPORT jintArray
JNICALL
Java_com_wzjing_face_opencvcamera_CameraView_nativeProcess(JNIEnv *env, jobject instance, jint row,
                                                           jint col, int count, jbyteArray data_);

void put(Mat* frame, int row, int col, int count, double* values);

void detectFace(Mat *frame);

void detectAndDraw( Mat& frame, CascadeClassifier& cascade, bool tryflip );


#endif //FACE_NATIVE_LIB_H