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

#define ATAG "opencv_native"
#define LOGI(TAG, format, ...) __android_log_print(ANDROID_LOG_INFO, TAG, format, ## __VA_ARGS__)

using namespace cv;
using namespace std;

CascadeClassifier classifier;
bool loaded = false;

/**
 * detect human face in a Mat
 * @param env   Java environment pointer
 * @param mat   Mat frame
 */
extern "C"
JNIEXPORT jobject JNICALL
Java_com_wzjing_face_opencvcamera_CameraView_nativeProcess(JNIEnv *env, jobject instance, jint w,
                                                           jint h, jbyteArray data_);

void detectFace(Mat *frame);

void detectAndDraw( Mat& frame, CascadeClassifier& cascade, bool tryflip );

#endif //FACE_NATIVE_LIB_H