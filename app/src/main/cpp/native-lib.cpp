#include "native-lib.h"

JNIEXPORT jstring JNICALL
Java_com_wzjing_face_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

JNIEXPORT void JNICALL
Java_com_wzjing_face_OpenCVCameraActivity_rotateFrame(JNIEnv *env, jobject /* this */, jlong frame, jfloat degree) {
    cv::Mat* mat = (cv::Mat *) frame;
    LOGI(ATAG, "frame_size:%d x %d\n", mat->rows, mat->cols);
    cvCvtColor(mat, mat, CV_BGR2GRAY);
//    cvFlip(mat, mat, 1);
}