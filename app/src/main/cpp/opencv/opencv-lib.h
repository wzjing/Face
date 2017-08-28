#ifndef FACE_OPENCV_LIB_H
#define FACE_OPENCV_LIB_H

#include <time.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/objdetect.hpp>
#include "../native-utils.h"

#define CLOCKS_PER_MILLSEC 1000

using namespace cv;

void detectAndDraw( Mat& frame );

#endif //FACE_OPENCV_LIB_H
