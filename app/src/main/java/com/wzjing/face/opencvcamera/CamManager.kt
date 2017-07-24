package com.wzjing.face.opencvcamera

import android.content.Context

abstract class CamManager {

    public var mPreviewListener: OnPreviewListener? = null
    protected var mSize: Size
    protected var mContext: Context

    constructor(context: Context) {
        mContext = context
        mSize = Size(1280, 720)
    }
    constructor(context: Context, size: Size): this(context) {
        mSize = size
    }

    /**
     * Open camera devide
     */
    public abstract fun openCamera()

    /**
     * Close camera device
     */
    public abstract fun closeCamera()

    /**
     * Start recording video
     */
    public abstract fun startRecord()

    /**
     * Stop recording video
     */
    public abstract fun stopRecord()

    /**
     * Set the preview and record video frame size
     */
    public fun setFrameSize(size: Size) {
        mSize = size
    }

    /**
     * PreviewListener interface to handle the ByteArray frame data
     */
    public interface OnPreviewListener {
        /**
         *
         */
        fun onPreview(w: Int, h: Int, data: ByteArray): Unit
    }

    /**
     * data class Size, storage width and height
     */
    public data class Size(val width: Int, val height: Int)
}