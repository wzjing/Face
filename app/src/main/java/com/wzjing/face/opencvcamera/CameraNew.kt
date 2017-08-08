package com.wzjing.face.opencvcamera

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.net.Uri
import android.util.Log
import android.widget.Toast

@TargetApi(21)
class CameraNew : CamManager{

    private val TAG = "CameraNew"

    private val STATE_PREVIEW = 0
    private val STATE_WAITING_LOCK = 1
    private val StATE_WAITING_PRECAPTURE = 2
    private val STATE_WAITING_NON_PRECAPTURE = 3
    private val STATE_PICTURE_TAKEN = 4
    private var mState = 0

    private var cameraManager: CameraManager
    private var mCameraID: String
    private var mCameraDevice: CameraDevice? = null
    private var mCaptureSession: CameraCaptureSession? = null
    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null
    private var mPreviewRequest: CaptureRequest? = null
    private var mImageReader: ImageReader? = null


    constructor(context: Context) : super(context)
    constructor(context: Context, size: Size): super(context, size)

    init {
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        mCameraID = cameraManager.cameraIdList[0]
    }

    @SuppressLint("MissingPermission")
    override fun openCamera() {
        mImageReader = ImageReader.newInstance(size.width, size.height, ImageFormat.YUV_420_888, 2)
        mImageReader?.setOnImageAvailableListener(mOnImageAvailableListener, null)
        cameraManager.openCamera(mCameraID, myStateCallback, null)
    }

    override fun closeCamera() {
        super.closeCamera()
        mCaptureSession?.close()
        mCameraDevice?.close()
        mImageReader?.close()
    }

    override fun startRecord() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stopRecord() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun takePicture(after: (Uri) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createPreviewSession() {
        Log.d(TAG, "createPreviewSession()")
        try {
            mPreviewRequestBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewRequestBuilder!!.addTarget(mImageReader!!.surface)
            mCameraDevice?.createCaptureSession(listOf(mImageReader!!.surface), captureSessionStateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val captureSessionCaptureCallback = object: CameraCaptureSession.CaptureCallback() {

        private fun process(result: CaptureResult?) {
            when (mState) {
                STATE_WAITING_LOCK-> {

                }
                StATE_WAITING_PRECAPTURE-> {

                }
                STATE_WAITING_NON_PRECAPTURE-> {

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

    private val captureSessionStateCallback = object: CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession?) {
            Toast.makeText(context, "Camera Configure failed", Toast.LENGTH_SHORT).show()
        }

        override fun onConfigured(session: CameraCaptureSession?) {
            mCaptureSession = session
            mPreviewRequestBuilder!!.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO)
            mPreviewRequest = mPreviewRequestBuilder!!.build()
            mCaptureSession!!.setRepeatingRequest(mPreviewRequest, null, null)
        }

    }

    private val myStateCallback = object: CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice?) {
            mCameraDevice = camera
            createPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice?) {
            camera?.close()
            mCameraDevice = null
        }

        override fun onError(camera: CameraDevice?, error: Int) {
            camera?.close()
            mCameraDevice = null
            Toast.makeText(context, "Camera open error", Toast.LENGTH_SHORT).show()
        }
    }

    private val mOnImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        val image = reader?.acquireNextImage()

        //Y data, (frame.width x frame.height)
        val y_buffer = image!!.planes[0].buffer
        val y_data = ByteArray(y_buffer.remaining())
        y_buffer.get(y_data)

        //UV data, (y_data.size/2 -1) : uvuvuv...uvu
        val uv_buffer = image.planes[1].buffer
        val uv_data = ByteArray(uv_buffer.remaining())
        uv_buffer.get(uv_data)

        //VU data, (y_data.size/2-1) : vuvuvu...vuv
        val vu_buffer = image.planes[2].buffer
        val vu_data = ByteArray(vu_buffer.remaining())
        vu_buffer.get(vu_data)

        //Send NV21 format ByteArray
        previewListener?.invoke(size.width, size.height, y_data.plus(vu_data.plus(uv_data[uv_data.size - 1])))
        image.close()
    }
}