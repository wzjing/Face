package com.wzjing.face.opencvcamera

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.ImageReader
import android.net.Uri
import android.util.Size
import android.view.TextureView
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
    private var mPreviewTexture: TextureView? = null
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    val myStateCallback = object: CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice?) {
            mCameraDevice = camera
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

    val mOnImageAvailableListener = object : ImageReader.OnImageAvailableListener{
        override fun onImageAvailable(reader: ImageReader?) {
            val image = reader?.acquireNextImage()

            //Y data
            val y_buffer = image!!.planes[0].buffer
            val y_data = ByteArray(y_buffer.remaining())
            y_buffer.get(y_data)

        }

    }
}