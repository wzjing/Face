package com.wzjing.face.opencvcamera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import org.opencv.imgproc.Imgproc

class CameraView(context: Context, var attrs: AttributeSet? = null) : SurfaceView(context, attrs), SurfaceHolder.Callback{

    private var camManager: CamManager = CamManager.Companion.Builder(context).build()
    public var frameListener: ((Mat)->Mat)? = null
    private var mCacheBitmap: Bitmap? = null

    private val paint: Paint = Paint()


    init {
        mCacheBitmap = Bitmap.createBitmap(camManager.size.width, camManager.size.height, Bitmap.Config.RGB_565)
        camManager.previewListener = {w, h, data ->
        }
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        camManager.openCamera()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        camManager.closeCamera()
    }

}