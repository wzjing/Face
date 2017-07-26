package com.wzjing.face.opencvcamera

import android.app.Notification
import android.content.Context
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.net.Uri

@Suppress("DEPRECATION")
class CameraOld : CamManager {

    constructor(context: Context) : super(context)
    constructor(context: Context, size: Size) : super(context, size)


    private var camera: Camera? = null

    override fun openCamera() {
        camera = Camera.open()
        val params = camera?.parameters
        params?.setPreviewSize(size.width, size.height)
        if (params?.supportedFocusModes?.contains(Camera.Parameters.FOCUS_MODE_AUTO) ?: false) {
            params?.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
        }
        camera?.parameters = params
        camera?.startPreview()
        if (previewListener != null) {
            camera?.setPreviewCallback { data, camera ->
                previewListener?.invoke(size.width, size.height, data)
            }
        }
    }

    override fun closeCamera() {
        super.closeCamera()
        camera?.stopPreview()
        camera?.setPreviewCallback(null)
        camera?.release()
        camera = null
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
}