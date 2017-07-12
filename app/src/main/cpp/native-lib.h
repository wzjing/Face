#ifndef FACE_NATIVE_LIB_H
#define FACE_NATIVE_LIB_H

#include <string>
#include <jni.h>
#include <android/log.h>
#include <opencv2/core/mat.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/core/core.hpp>

#define ATAG "opencv_native"
#define LOGI(TAG, format, ...) __android_log_print(ANDROID_LOG_INFO, TAG, format, ## __VA_ARGS__)

extern "C" {
JNIEXPORT jstring JNICALL
Java_com_wzjing_face_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */);

JNIEXPORT void JNICALL
Java_com_wzjing_face_OpenCVCameraActivity_rotateFrame(JNIEnv *env, jobject /* this */, jlong frame, jfloat degree);
};

#endif //FACE_NATIVE_LIB_H
