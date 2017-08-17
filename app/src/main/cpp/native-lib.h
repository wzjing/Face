#ifndef FACE_NATIVE_LIB_H
#define FACE_NATIVE_LIB_H

#include "GLESView.h"
#include <string>
#include <jni.h>
#include <iostream>
#include <bitset>
#include <vector>
#include <android/bitmap.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/objdetect.hpp>
#include "native-util.h"

using namespace cv;
using namespace std;

CascadeClassifier classifier;
bool loaded = false;

extern "C"
JNIEXPORT void
JNICALL
Java_com_wzjing_face_opencvcamera_CameraView_nativeProcess(JNIEnv *env, jobject instance, jint row,
                                                           jint col, jint count, jbyteArray data_, jobject bitmap, jboolean faceDetection);

void detectAndDraw( Mat& frame );


#endif //FACE_NATIVE_LIB_H