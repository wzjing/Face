#include <android/bitmap.h>
#include "native-lib.h"

float start = 0.0f;
#define CLOCKS_PER_MILLSEC 1000

char *pix16(char *data) {
    char pixs[16][255];
    for (int i = 0; i < 16; i++) {
        sprintf(pixs[i], "%s %02x", i < 1 ? "" : pixs[i - 1], data[i]);
    }
    return pixs[15];
}

char *pix16(uchar *data) {
    char pixs[16][255];
    for (int i = 0; i < 16; i++) {
        sprintf(pixs[i], "%s %02x", i < 1 ? "" : pixs[i - 1], data[i]);
    }
    return pixs[15];
}

JNIEXPORT void
JNICALL
Java_com_wzjing_face_opencvcamera_CameraView_nativeProcess(JNIEnv *env, jobject instance, jint row,
                                                           jint col, jint count, jbyteArray data_,
                                                           jobject bitmap, jboolean faceDetection) {
    start = clock();
    LOGI(TAG, "nativeProcess()-----------------------------------------------");
    char *data = (char *) env->GetPrimitiveArrayCritical(data_, 0);

//    LOGI(TAG, "%-12s: %s", "origin", pix16(data));

    // Generate new Mat
    Mat frame = Mat((int) (row * 1.5), col, CV_8UC1);

    LOGD(TAG, "nativeProcess(): copy data");
    memcpy(frame.data, data, count * sizeof(char));
    LOGD(TAG, "frame info: row:%d col:%d, channel:%d", frame.rows, frame.cols, frame.channels());
//    LOGI(TAG, "%-12s: %s", "frame", pix16(frame.data));
    Mat rgb;
    cvtColor(frame, rgb, COLOR_YUV2RGB_NV21, 3);
    LOGD(TAG, "rgb info: row:%d col:%d, channel:%d", rgb.rows, rgb.cols, rgb.channels());
    LOGI(TAG, "%-12s: %s", "rgb", pix16(rgb.data));
    env->ReleasePrimitiveArrayCritical(data_, data, JNI_ABORT);
    LOGI(TAG, "Step 1: %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);

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
        LOGD(TAG, "nativeProcess(): Bitmap Info: %d x %d <format: %d>", bmp_info.width,
             bmp_info.height,
             bmp_info.format);
    void *bmp_pixels = 0;
    if (AndroidBitmap_lockPixels(env, bitmap, &bmp_pixels) < 0) {
        LOGE(TAG, "nativeProcess(): Unable to lock bitmap pixels");
        return;
    }

    if (!bmp_pixels) {
        LOGE(TAG, "nativeProcess(): didn't get any pixels");
        return;
    }
    LOGI(TAG, "Step 2: %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);

    // Copy data to bitmap
    Mat bmpMat(bmp_info.height, bmp_info.width, CV_8UC2, bmp_pixels);
    cvtColor(rgb, bmpMat, COLOR_RGB2BGR565);
    LOGD(TAG, "bmpMat info: row:%d col:%d, channel:%d", bmpMat.rows, bmpMat.cols,
         bmpMat.channels());
    if (faceDetection)
        detectAndDraw(bmpMat);
    AndroidBitmap_unlockPixels(env, bitmap);
    LOGD(TAG, "nativeProcess(): finished %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);
}

void detectAndDraw(Mat &frame) {
    if (!loaded) {
        loaded = classifier.load("/storage/emulated/0/classifier.xml");
    }
    LOGI(TAG, "Detection Start: %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);
    vector<Rect> faces;
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

    cvtColor(frame, gray, COLOR_BGR5652GRAY);
    LOGI(TAG, "Detection Pre-gray: %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);

    double ratio = 180.0 / frame.rows;
    int minSize = (int) (180 * ratio);
    resize(gray, small_gray, Size(), ratio, ratio, INTER_LINEAR);
    LOGI(TAG, "Detection Pre-resize: %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);
    equalizeHist(small_gray, small_gray);
    LOGI(TAG, "Detection Pre-equal: %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);
    rotate(small_gray, small_gray, ROTATE_90_CLOCKWISE);

    classifier.detectMultiScale(small_gray,
                                faces,
                                1.5,
                                3,
                                0 |
                                CASCADE_SCALE_IMAGE,//|CASCADE_FIND_BIGGEST_OBJECT|CASCADE_DO_ROUGH_SEARCH
                                Size(minSize, minSize),
                                Size((int) (small_gray.cols * 0.6), (int) (small_gray.rows * 0.6)));


    LOGI(TAG, "Detection Draw: %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);
    for (size_t i = 0; i < faces.size(); i++) {
        Scalar color = colors[i % 8];

        int tx = (int) (faces[i].y / ratio);
        int ty = (int) ((small_gray.cols - faces[i].x - faces[i].width) / ratio);
        int w = (int) (faces[i].height / ratio);
        int h = (int) (faces[i].width / ratio);
        rectangle(frame, Rect(tx, ty, w, h), color, 3, 8, 0);
    }
    LOGE(TAG, "Detect number: %d", faces.size());
    LOGI(TAG, "Detection End: %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);
}