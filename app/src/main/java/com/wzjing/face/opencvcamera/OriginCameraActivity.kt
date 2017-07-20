package com.wzjing.face.opencvcamera

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import android.view.View
import android.widget.Toast
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

@TargetApi(21)
class OriginCameraActivity : AppCompatActivity() {

    private lateinit var mCameraID: String
    private var mCameraDevice: CameraDevice? = null
    private var mCaptureSession: CameraCaptureSession? = null
    private var mImageReader: ImageReader? = null
    private lateinit var mPreviewSize: Size

    private var mCameraLock = Semaphore(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openCamera()

    }

    override fun onPause() {
        closeCamera()
        super.onPause()

    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        val cameraManager = applicationContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        mCameraID = cameraManager.cameraIdList.get(0)
        try {
            if (!mCameraLock.tryAcquire(2500, TimeUnit.MICROSECONDS))
                throw RuntimeException("Time out of waiting lock camera")
            cameraManager.openCamera(mCameraID, myStateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            throw RuntimeException("lock camera was interrupted")
        }
    }

    private fun closeCamera() {
        try {
            mCameraLock.acquire()
            if (mCaptureSession != null) {
                mCaptureSession?.close()
                mCaptureSession = null
            }
            if (mCameraDevice != null) {
                mCameraDevice?.close()
                mCameraDevice = null
            }
            if (mImageReader != null) {
                mImageReader?.close()
                mImageReader = null
            }
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera on close")
        } finally {
            mCameraLock.release()
        }
    }

    private fun startBackgroundThread() {
        TODO("Start the background handle thread")
    }

    private fun stopBackgroundThread() {
        TODO("Stop the background handle thread")
    }

    private fun createPreiewSession() {
        try {
            TODO("Configure the camera preview and origin data")
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val myStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice?) {
            mCameraLock.release()
            mCameraDevice = camera
            createPreiewSession()
        }

        override fun onDisconnected(camera: CameraDevice?) {
            mCameraLock.release()
            camera?.close()
            mCameraDevice = null
        }

        override fun onError(camera: CameraDevice?, error: Int) {
            mCameraLock.release()
            camera?.close()
            mCameraDevice = null
            Toast.makeText(this@OriginCameraActivity, "Camera Error: $error", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

}
