#ifndef FACE_NATIVE_LIB_H
#define FACE_NATIVE_LIB_H

#include <string>
#include <jni.h>
#include <iostream>
#include <bitset>
#include <vector>
#include "native-utils.h"
#include "opengles/gl-utils.h"
#include "opencv/opencv-lib.h"

using namespace cv;
using namespace std;

extern "C"
{
JNIEXPORT void
JNICALL
Java_com_wzjing_face_opencvcamera_GLCameraView_nativeProcess(JNIEnv *env, jobject instance,
                                                             jint row,
                                                             jint col, jint count, jbyteArray data_,
                                                             jobject bitmap,
                                                             jboolean faceDetection);

JNIEXPORT void
JNICALL Java_com_wzjing_face_opencvcamera_GLCameraView_initGLES(JNIEnv *env, jobject obj, jint w,
                                                                jint h, jobject bitmap);

JNIEXPORT void
JNICALL Java_com_wzjing_face_opencvcamera_GLCameraView_step(JNIEnv *env, jobject);
}

bool setGraphics(JNIEnv *env, int w, int h);

void renderFrame();

#endif //FACE_NATIVE_LIB_H