#include <android/bitmap.h>
#include "native-lib.h"

long start = 0;

char* pix16(char * data) {
    char pixs [16][255];
    for (int i = 0; i < 16; i++) {
        sprintf(pixs[i], "%s %02x", i<1?"":pixs[i-1], data[i]);
    }
    return pixs[15];
}

JNIEXPORT void
JNICALL
Java_com_wzjing_face_opencvcamera_CameraView_nativeProcess(JNIEnv *env, jobject instance, jint row,
                                                           jint col, jint count, jbyteArray data_,
                                                           jobject bitmap) {
    LOGI(TAG, "nativeProcess()-----------------------------------------------");
    char *data = (char *) env->GetPrimitiveArrayCritical(data_, 0);

//    LOGI(TAG, "%-16s: %s", "origin", pix16(data));

    // Generate new Mat
    Mat frame = Mat((int) (row * 1.5), col, CV_8UC1);
    LOGD(TAG, "nativeProcess(): Put mat data");
    mat_put<char>(&frame, frame.rows, frame.cols, count, data);
    LOGD(TAG, "frame info: row:%d col:%d, channel:%d", frame.rows, frame.cols, frame.channels());
//    LOGI(TAG, "%-16s: %s", "frame", pix16(frame.data));
    Mat rgb;
    cvtColor(frame, rgb, COLOR_YUV2RGB_NV21, 3);
    LOGD(TAG, "rgb info: row:%d col:%d, channel:%d", rgb.rows, rgb.cols, rgb.channels());
//    LOGI(TAG, "%-16s: %s", "rgb", pix16(rgb.data));
    env->ReleasePrimitiveArrayCritical(data_, data, JNI_ABORT);

    // Get Bitmap data
    if (bitmap == NULL) {
        LOGE(TAG, "Bitmap is NULL");
        return;
    }
    AndroidBitmapInfo bmp_info = {0};
    if (AndroidBitmap_getInfo(env, bitmap, &bmp_info) < 0) {
        LOGD(TAG, "nativeProcess(): Unable to get bitmap info");
        return;
    } else
        LOGD(TAG, "nativeProcess(): Bitmap Info: %d x %d <format: %d>", bmp_info.width, bmp_info.height,
             bmp_info.format);
    LOGD(TAG, "nativeProcess(): got Bitmap info");
    void *bmp_pixels = 0;
    if (AndroidBitmap_lockPixels(env, bitmap, &bmp_pixels) < 0) {
        LOGE(TAG, "nativeProcess(): Unable to lock bitmap pixels");
        return;
    } else
        LOGD(TAG, "nativeProcess(): got Bitmap pixels");

    if (!bmp_pixels) {
        LOGE(TAG, "nativeProcess(): didn't get any pixels");
        return;
    }

    LOGE(TAG, "BMP before: %s", pix16((char*)bmp_pixels));
    // Copy data to bitmap
    LOGD(TAG, "nativeProcess(): creating tmp Mat");
    Mat tmp(bmp_info.height, bmp_info.width, CV_8UC2, bmp_pixels);
    LOGD(TAG, "nativeProcess(): copying data");
    cvtColor(rgb, tmp, COLOR_RGB2BGR565, 3);
    LOGD(TAG, "nativeProcess(): copy finished");
    LOGD(TAG, "tmp info: row:%d col:%d, channel:%d", tmp.rows, tmp.cols, tmp.channels());
//    LOGI(TAG, "%-16s: %s", "tmp", pix16(tmp.data));

    LOGE(TAG, "BMP after: %s", pix16((char*)bmp_pixels));
    AndroidBitmap_unlockPixels(env, bitmap);
    LOGD(TAG, "nativeProcess(): return");
}

template<typename T>
void mat_put(Mat *m, int row, int col, int count, char *buff) {
    LOGD(TAG, "mat_put(): T size: %d", sizeof(T));

    if (!m) return;
    if (!buff) return;

    LOGD(TAG, "mat_put(): Step 1");
    count *= sizeof(T);
    int rest = ((m->rows - row) * m->cols - col) * (int) m->elemSize();
    if (count > rest) count = rest;

    LOGD(TAG, "mat_put(): Step 3");
    if (m->isContinuous()) {
        memcpy(m->ptr(row, col), buff, count);
    } else {
        // row by row
        int num = (m->cols - col) * (int) m->elemSize(); // 1st partial row
        if (count < num) num = count;
        uchar *data = m->ptr(row++, col);
        while (count > 0) {
            memcpy(data, buff, num);
            count -= num;
            buff += num;
            num = m->cols * (int) m->elemSize();
            if (count < num) num = count;
            data = m->ptr(row++, 0);
        }
    }

    LOGD(TAG, "mat_put(): Finished");
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

    double fx = 180.0 / frame.cols;
    int minSize = (int) (180 * fx);
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
                             0 |
                             CASCADE_SCALE_IMAGE,//|CASCADE_FIND_BIGGEST_OBJECT|CASCADE_DO_ROUGH_SEARCH
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