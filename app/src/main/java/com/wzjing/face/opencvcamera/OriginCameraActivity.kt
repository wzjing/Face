package com.wzjing.face.opencvcamera

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import android.widget.Toast
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

@TargetApi(21)
class OriginCameraActivity : AppCompatActivity() {

    private lateinit var mCameraID: String
    private var mCameraDevice: CameraDevice? = null
    private lateinit var mCaptureSession: CameraCaptureSession
    private lateinit var mPreviewSize: Size

    private var mCameraLock = Semaphore(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initCamera()

    }

    @SuppressLint("MissingPermission")
    private fun initCamera() {
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

    private fun createPreiewSession() {
        try {

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
