@file:Suppress("DEPRECATION")

package com.wzjing.face.opencvcamera

import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.net.Uri
import android.util.Log

@Suppress("DEPRECATION")
class CameraOld : CamManager {

    private val TAG = "CameraOld"

    constructor(context: Context) : super(context)
    constructor(context: Context, size: Size) : super(context, size)

    private var camera: Camera? = null
    private var previewTexture: SurfaceTexture? = null
    private var previewTextureId: Int = 20170808

    override fun openCamera() {
        camera = Camera.open()
        Log.d(TAG, "camera is null: ${camera == null}")
        val params = camera?.parameters
        params?.setPreviewSize(size.width, size.height)
        if (params?.supportedFocusModes?.contains(Camera.Parameters.FOCUS_MODE_AUTO) ?: false) {
            params?.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
        }
        camera?.parameters = params
        previewTexture = SurfaceTexture(previewTextureId)
        camera?.setPreviewTexture(previewTexture)
        camera?.startPreview()
        camera?.setPreviewCallback { data, _ ->
            Log.d(TAG, "Previewing")
            previewListener?.invoke(size.width, size.height, data)
        }
    }

    override fun closeCamera() {
        super.closeCamera()
        camera?.stopPreview()
        camera?.setPreviewCallback(null)
        camera?.release()
        camera = null
        previewTexture?.release()
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