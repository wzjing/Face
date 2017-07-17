#include "native-lib.h"

JNIEXPORT void JNICALL
Java_com_wzjing_face_opencvcamera_OpenCVCameraActivity_rotateFrame(JNIEnv *env, jobject /* this */, jlong frame, jfloat degree) {

    Mat* src = (Mat *) frame;
    CascadeClassifier classifier;
    classifier.load("/storage/emulated/0/classifier.xml");
    transpose(*src, *src);
    flip(*src, *src, 1);
    detectAndDraw(*src, classifier, true);
    transpose(*src, *src);
    flip(*src, *src, 0);
}

void detectAndDraw( Mat& frame, CascadeClassifier& cascade, bool tryflip )
{
    double t = 0;
    std::vector<Rect> faces, faces2;
    const static Scalar colors[] =
            {
                    Scalar(255,0,0),
                    Scalar(255,128,0),
                    Scalar(255,255,0),
                    Scalar(0,255,0),
                    Scalar(0,128,255),
                    Scalar(0,255,255),
                    Scalar(0,0,255),
                    Scalar(255,0,255)
            };
    Mat gray, smallImg;

    cvtColor( frame, gray, COLOR_BGR2GRAY );
    double fx = 1.0;
    resize( gray, smallImg, Size(), fx, fx, INTER_LINEAR );
    equalizeHist( smallImg, smallImg );

    t = (double)getTickCount();
    cascade.detectMultiScale( smallImg, faces,
                              1.1, 3, 0
                                      //|CASCADE_FIND_BIGGEST_OBJECT
                                      //|CASCADE_DO_ROUGH_SEARCH
                                      |CASCADE_SCALE_IMAGE,
                              Size(100, 100) );
    if( tryflip )
    {
        flip(smallImg, smallImg, 1);
        cascade.detectMultiScale( smallImg, faces2,
                                  1.1, 2, 0
                                          //|CASCADE_FIND_BIGGEST_OBJECT
                                          //|CASCADE_DO_ROUGH_SEARCH
                                          |CASCADE_SCALE_IMAGE,
                                  Size(30, 30) );
        for( std::vector<Rect>::const_iterator r = faces2.begin(); r != faces2.end(); ++r )
        {
            faces.push_back(Rect(smallImg.cols - r->x - r->width, r->y, r->width, r->height));
        }
    }
    t = (double)getTickCount() - t;
    LOGI( ATAG, "detection time = %g ms\n", t*1000/getTickFrequency());
    for ( size_t i = 0; i < faces.size(); i++ )
    {
        Rect r = faces[i];
        Mat smallImgROI;
        std::vector<Rect> nestedObjects;
        Scalar color = colors[i%8];

        rectangle(frame, cvPoint(cvRound(r.x), cvRound(r.y)),
                  cvPoint(cvRound((r.x + r.width - 1)), cvRound((r.y + r.height - 1))),
                  color, 4, 8, 0);
    }
}