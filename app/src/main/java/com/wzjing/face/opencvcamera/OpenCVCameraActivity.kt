package com.wzjing.face.opencvcamera

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.wzjing.face.R
import org.opencv.android.*
import org.opencv.core.*
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream

class OpenCVCameraActivity : Activity(), CameraBridgeViewBase.CvCameraViewListener {

    private val TAG = "OpenCVCameraActivity"
    private var openCvCameraView: CameraBridgeViewBase? = null
    private var cascadeClassifier: CascadeClassifier? = null
    private var grayscaleImage: Mat? = null
    private var absoluteFaceSize: Int = 0

    private val mLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {

            super.onManagerConnected(status)
            openCvCameraView!!.enableView()
        }
    }

    private fun initializeOpenCVDependencies() {

        try {
            // Copy the resource into a temp file so OpenCV can load it
            val stream = resources.openRawResource(R.raw.cascade_classifier)
            val cascadeDir = getDir("cascade", Context.MODE_PRIVATE)
            val mCascadeFile = File(cascadeDir, "lbpcascade_frontalface.xml")
            val os = FileOutputStream(mCascadeFile)


            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (true) {
                bytesRead = stream.read(buffer)
                if (bytesRead == -1)
                    break

                os.write(buffer, 0, bytesRead)
            }
            stream.close()
            os.flush()
            os.close()

            // Load the cascade classifier
            cascadeClassifier = CascadeClassifier(mCascadeFile.absolutePath)
        } catch (e: Exception) {
            Log.e("OpenCVActivity", "Error loading cascade", e)
        }

        // And we are ready to go
        openCvCameraView!!.enableView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)

        openCvCameraView = JavaCameraView(this, -1)
        setContentView(openCvCameraView)
        openCvCameraView!!.setCvCameraViewListener(this)
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        grayscaleImage = Mat(height, width, CvType.CV_8UC4)

        // The faces will be a 20% of the height of the screen
        absoluteFaceSize = (height * 0.2).toInt()
    }

    override fun onCameraViewStopped() {}

    override fun onCameraFrame(aInputFrame: Mat): Mat {
        // Create a grayscale image
//        Imgproc.cvtColor(aInputFrame, grayscaleImage, Imgproc.COLOR_RGBA2RGB)
//
//        val faces = MatOfRect()
//
//        // Use the classifier to detect faces
//        if (cascadeClassifier != null) {
//            cascadeClassifier!!.detectMultiScale(grayscaleImage, faces, 1.1, 2, 2,
//                    Size(absoluteFaceSize.toDouble(), absoluteFaceSize.toDouble()), Size())
//        }
//
//        // If there are any faces found, draw a rectangle around it
//        val facesArray = faces.toArray()
//        for (i in facesArray.indices)
//            Imgproc.rectangle(aInputFrame, facesArray[i].tl(), facesArray[i].br(), Scalar(0.0, 255.0, 0.0, 255.0), 3)

        Log.i(TAG, "______________________________________________")
        rotateFrame(aInputFrame.nativeObj, -90)
//        Imgproc.rectangle(aInputFrame, Point(100.0, 100.0), Point(200.0, 200.0), Scalar(255.0, 0.0, 0.0), 5)
        Log.i(TAG, "rotate finished")
        return aInputFrame
    }

    public override fun onResume() {
        super.onResume()
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback)
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
    }

    external fun rotateFrame(frame: Long, degree: Int)

    companion object {

        init {
            System.loadLibrary("opencv_java3")
            System.loadLibrary("native-lib")
        }
    }
}
