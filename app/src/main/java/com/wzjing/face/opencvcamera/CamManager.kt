package com.wzjing.face.opencvcamera

import android.content.Context
import android.net.Uri
import android.os.Build

abstract class CamManager (var context: Context, var size: Size = Size(1280, 720)){

    /**
     * mPreviewListener oprate the origin Bytearray of frame
     * default format is YUV_8888
     * @see android.graphics.ImageFormat
     * @param   Int   frame width
     * @param   Int   frame height
     * @param   ByteArray    frame ByteArray data
     */
    public var previewListener: ((Int, Int, ByteArray)->Unit)? = null

    /**
     * Open camera device
     */
    public abstract fun openCamera()

    /**
     * Close camera device
     */
    public open fun closeCamera() {
        previewListener = null
    }

    /**
     * Start recording video
     */
    public abstract fun startRecord()

    /**
     * Stop recording video
     */
    public abstract fun stopRecord()

    /**
     * Take a picture
     * @param   after   excute after() when finish take picture
     */
    public abstract fun takePicture(after: (Uri)->Unit)

    /**
     * data class Size, storage width and height
     */
    public data class Size(val width: Int, val height: Int)

    public companion object {
        public class Builder(var context: Context, val size: Size? = null) {
            public fun build(): CamManager {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
                    if (size != null) return CameraNew(context, size) else return CameraNew(context)
                else
                    if (size != null) return CameraOld(context, size) else return CameraOld(context)
            }
        }
    }
}