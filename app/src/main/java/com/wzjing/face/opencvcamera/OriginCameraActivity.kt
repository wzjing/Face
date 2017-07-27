package com.wzjing.face.opencvcamera

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

@TargetApi(21)
class OriginCameraActivity : AppCompatActivity() {

    private val TAG = "OriginCameraActivity"

    private val STATE_PREVIEW = 0
    private val STATE_WAITING_LOCK = 1
    private val StATE_WAITING_PRECAPTURE = 2
    private val STATE_WAITING_NON_PRECAPTURE = 3
    private val STATE_PICTURE_TAKEN = 4
    private var mState = 0

    private var mCVView: CVView? = null
    private var mPreviewTexture: TextureView? = null
    private lateinit var mCameraID: String
    private var mCameraDevice: CameraDevice? = null
    private var mCaptureSession: CameraCaptureSession? = null
    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null
    private var mPreviewRequest: CaptureRequest? = null
    private var mImageReader: ImageReader? = null
    private lateinit var mPreviewSize: Size

    private var mCameraLock = Semaphore(1)

    /* Life Cycle */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = RelativeLayout(this)
        mPreviewTexture = TextureView(this)
        mCVView = CVView(this)
        val params1 = RelativeLayout.LayoutParams(720, 1280)
        params1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
        params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        val params2 = RelativeLayout.LayoutParams(640, 360)
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)

        layout.addView(mPreviewTexture, params1)
        layout.addView(mCVView, params2)
        setContentView(layout, ViewGroup.LayoutParams(-1, -1))
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (mPreviewTexture!!.isAvailable) {
            openCamera()
        } else {
            mPreviewTexture!!.surfaceTextureListener = mSurfaceTextureListener
        }
    }

    override fun onPause() {
        closeCamera()
        stopBackgroundThread()
        super.onPause()
    }

    override fun onDestroy() {
        closeCamera()
        stopBackgroundThread()
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        Log.d(TAG, "openCamera()")
        setUpOutputs()
        val cameraManager = applicationContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        mCameraID = cameraManager.cameraIdList[0]
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
        Log.d(TAG, "closeCamera()")
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

    /* use background thread to handle image process */
    private var mBackgroundThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null

    private fun startBackgroundThread() {
        Log.d(TAG, "startBackgroundThread()")
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread?.start()
        mBackgroundHandler = Handler(mBackgroundThread?.looper)
    }

    private fun stopBackgroundThread() {
        Log.d(TAG, "stopBackgroundThread")
        mBackgroundThread?.quitSafely()
        try {
            mBackgroundThread?.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun setUpOutputs() {
        Log.d(TAG, "setUpOutputs")
        mImageReader = ImageReader.newInstance(1280, 720, ImageFormat.YUV_420_888, 2)
        mImageReader?.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler)
    }

    private fun createPreviewSession() {
        Log.d(TAG, "createPreviewSession()")
        try {
            val texture = mPreviewTexture!!.surfaceTexture
            texture.setDefaultBufferSize(1280, 720)
            val surface = Surface(texture)
            mPreviewRequestBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewRequestBuilder!!.addTarget(surface)
            mPreviewRequestBuilder!!.addTarget(mImageReader!!.surface)
            mCameraDevice?.createCaptureSession(listOf(surface, mImageReader!!.surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession?) {
                    Log.d(TAG, "CameraCaptureSession.StateCallback.onConfigured()")
                    mCaptureSession = session
                    mPreviewRequestBuilder!!.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                    mPreviewRequest = mPreviewRequestBuilder!!.build()
                    mCaptureSession!!.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler)
                }

                override fun onConfigureFailed(session: CameraCaptureSession?) {
                    Toast.makeText(this@OriginCameraActivity, "Preiew failed", Toast.LENGTH_SHORT).show()
                }
            }, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val mOnImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        val image = reader?.acquireNextImage()

        //Y data
        val y_buffer = image!!.planes[0].buffer
        val y_data = ByteArray(y_buffer.remaining())
        y_buffer.get(y_data)
        Log.i(TAG, "Y byte: ${!y_data.isEmpty()}, size ${y_data.size}")

        //U data(Actully, it is uvuvuvuv), half size of y_buffer
        val u_buffer = image.planes[1].buffer
        val u_data = ByteArray(u_buffer.remaining())
        u_buffer.get(u_data)
        Log.i(TAG, "U byte: ${!u_data.isEmpty()}, size ${u_data.size}")

        //V data(Actully, it is vuvuvuvu), half size of y_buffer
        val v_buffer = image.planes[2].buffer
        val v_data = ByteArray(v_buffer.remaining())
        v_buffer.get(v_data)
        Log.i(TAG, "V byte: ${!v_data.isEmpty()}, size ${v_data.size}")

        val uv_data = v_data.plus(u_data[u_data.size - 1])
        Log.i(TAG, "V byte: ${!uv_data.isEmpty()}, size ${uv_data.size}")
        mCVView?.addFrame(1280, 720, y_data.plus(uv_data))


//        val u_str = StringBuilder()
//        for (i in 0..15) {
//            u_str.append(u_data[i].toInt().toString(16)+" ")
//        }
//        Log.i(TAG, "U: $u_str")
//
//        val v_str = StringBuilder()
//        for (i in 0..15) {
//            v_str.append(v_data[i].toInt().toString(16)+" ")
//        }
//        Log.i(TAG, "V: $v_str")

        image.close()
    }

    private val mSurfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {

        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {

        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            return true
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            openCamera()
        }

    }

    private val mCaptureCallback = object : CameraCaptureSession.CaptureCallback() {

        private fun process(result: CaptureResult?) {
            when (mState) {
                STATE_WAITING_LOCK -> {
                    val afState = result?.get(CaptureResult.CONTROL_AF_STATE)
                    if (afState == null) {
//                        TODO("Call capture picture here")
                    } else if (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED ||
                            afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
                        val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN
//                            TODO("Call capture picture here")
                        } else {
//                            TODO("Call pre capture work")
                        }
                    }
                }
                StATE_WAITING_PRECAPTURE -> {
                    val aeState = result?.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE
                    }
                }
                STATE_WAITING_NON_PRECAPTURE -> {
                    val aeState = result?.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null ||
                            aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN
//                        TODO("Call capture picture here")
                    }
                }
            }
        }

        override fun onCaptureProgressed(session: CameraCaptureSession?, request: CaptureRequest?, partialResult: CaptureResult?) {
            process(partialResult)
        }

        override fun onCaptureCompleted(session: CameraCaptureSession?, request: CaptureRequest?, result: TotalCaptureResult?) {
            process(result)
        }
    }

    private val myStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice?) {
            Log.d(TAG, "CameraDevice.StateCalback: onOpened()")
            mCameraLock.release()
            mCameraDevice = camera
            createPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice?) {
            Log.d(TAG, "CameraDevice.StateCalback: onDisconnected()")
            mCameraLock.release()
            camera?.close()
            mCameraDevice = null
        }

        override fun onError(camera: CameraDevice?, error: Int) {
            Log.d(TAG, "CameraDevice.StateCalback: onError()")
            mCameraLock.release()
            camera?.close()
            mCameraDevice = null
            Toast.makeText(this@OriginCameraActivity, "Camera Error: $error", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    companion object {

        init {
            System.loadLibrary("opencv_java3")
            System.loadLibrary("native-lib")
        }
    }

}
