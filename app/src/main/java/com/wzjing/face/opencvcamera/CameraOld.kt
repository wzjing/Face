package com.wzjing.face.opencvcamera

import android.content.Context
import android.hardware.Camera
import android.hardware.camera2.CameraManager

@Suppress("DEPRECATION")
class CameraOld : CamManager {

    constructor(context: Context) : super(context)
    constructor(context: Context, size: Size) : super(context, size)


    private var camera: Camera? = null

    override fun openCamera() {
        camera = Camera.open()
        val params = camera?.parameters
        params?.setPreviewSize(mSize.width, mSize.height)
        if (params?.supportedFocusModes?.contains(Camera.Parameters.FOCUS_MODE_AUTO) ?: false) {
            params?.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
        }
        camera?.parameters = params
        camera?.startPreview()
        if (mPreviewListener != null) {
            camera?.setPreviewCallback { data, camera ->
                mPreviewListener?.onPreview(mSize.width, mSize.height, data)
            }
        }
    }

    override fun closeCamera() {
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
}