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

    private val mLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {

            super.onManagerConnected(status)
            openCvCameraView!!.enableView()
            openCvCameraView!!.enableFpsMeter()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)

        openCvCameraView = JavaCameraView(this, JavaCameraView.CAMERA_ID_FRONT)
        setContentView(openCvCameraView)
        openCvCameraView!!.setMaxFrameSize(1280, 720)
        openCvCameraView!!.setCvCameraViewListener(this)
    }

    override fun onCameraViewStarted(width: Int, height: Int) {}

    override fun onCameraViewStopped() {}

    override fun onCameraFrame(aInputFrame: Mat): Mat {

        detectFaces(aInputFrame.nativeObj)
        return aInputFrame
    }

    public override fun onResume() {
        super.onResume()
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
    }

    external fun detectFaces(frame: Long)

    companion object {

        init {
            System.loadLibrary("opencv_java3")
            System.loadLibrary("native-lib")
        }
    }
}
