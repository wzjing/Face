#include "native-lib.h"

JNIEXPORT jstring JNICALL
Java_com_wzjing_face_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

JNIEXPORT void JNICALL
Java_com_wzjing_face_opencvcamera_OpenCVCameraActivity_rotateFrame(JNIEnv *env, jobject /* this */, jlong frame, jfloat degree) {
    using namespace cv;
    Mat* src = (Mat *) frame;
//    cvtColor(*src, *src, CV_BGR2GRAY);
    circle(*src, Point_<int>(100,200), 100, Scalar_<int>(255, 0, 0), 5);
//    transpose(*src, *src);
//    flip(*src, *src, 0);
}