package com.wzjing.face.opencvcamera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.SurfaceView
import org.opencv.android.JavaCameraView

public class CVView : SurfaceView {

    private var mCacheBitmap: Bitmap? =null
    private val paint: Paint
    private var threadRunning = true
    private var frameReady = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        paint = Paint()
        paint.isFilterBitmap = true

    }

    private fun drawMat(mat: Mat){
        Utils.matToBitmap(mat, mCacheBitmap)
        val w = mCacheBitmap?.width ?: 0
        val h= mCacheBitmap?.height ?: 0
        val canvas = holder.lockCanvas()
        if (canvas != null) {
            val src = Rect(0, 0, w, h)
            val dst = Rect(0, 0, canvas.width, canvas.height)
            canvas.drawBitmap(mCacheBitmap, src, dst, paint)
        }
        holder.unlockCanvasAndPost(canvas)
    }

    inner class CameraRender: Runnable {
        override fun run() {
            do {
                var hasFrame = false
                synchronized(this@CVView) {
                    while (!frameReady && threadRunning)
                        (this@CVView as java.lang.Object).wait()
                }
            } while (threadRunning)
        }

    }
}