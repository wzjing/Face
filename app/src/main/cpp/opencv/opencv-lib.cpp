#include "opencv-lib.h"

static CascadeClassifier classifier;
static bool loaded = 0;

void detectAndDraw(Mat &frame) {
    float start = clock();
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

    cvtColor(frame, gray, COLOR_RGBA2GRAY);
    LOGI(TAG, "Detection Pre-gray: %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);

    double ratio = 180.0 / frame.rows;
    int minSize = (int) (180 * ratio);
    resize(gray, small_gray, Size(), ratio, ratio, INTER_LINEAR);
    LOGI(TAG, "Detection Pre-resize: %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);
    equalizeHist(small_gray, small_gray);
    LOGI(TAG, "Detection Pre-equal: %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);

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

        // orignal unrotate mat draw position
//        int tx = (int) (faces[i].y / ratio);
//        int ty = (int) ((small_gray.cols - faces[i].x - faces[i].width) / ratio);
//        int w = (int) (faces[i].height / ratio);
//        int h = (int) (faces[i].width / ratio);
        int tx = (int) (faces[i].x / ratio);
        int ty = (int) (faces[i].y / ratio);
        int w = (int) (faces[i].width / ratio);
        int h = (int) (faces[i].height / ratio);
        rectangle(frame, Rect(tx, ty, w, h), color, 3, 8, 0);
    }
    LOGI(TAG, "Detect number: %d", faces.size());
    LOGI(TAG, "Detection End: %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);
}
