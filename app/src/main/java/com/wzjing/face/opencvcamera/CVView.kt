package com.wzjing.face.opencvcamera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

public class CVView : SurfaceView, SurfaceHolder.Callback {

    private val TAG = "CVView"

    private var mCacheBitmap: Bitmap? = null
    private val paint: Paint
    private var threadRunning = true
    private var frameReady = false
    private var frameArray: ArrayList<Mat>

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        paint = Paint()
        paint.isFilterBitmap = true
        frameArray = arrayListOf<Mat>()
        holder.addCallback(this)
    }

    public fun addFrame(frame: Mat) {
        if (!frame.empty()) {
            frameArray.add(frame)
            frameReady = true
        }
    }

    private fun drawMat() {
        if (frameArray.isNotEmpty()) {
            Utils.matToBitmap(frameArray[0], mCacheBitmap)
            val w = mCacheBitmap?.width ?: 0
            val h = mCacheBitmap?.height ?: 0
            val canvas = holder.lockCanvas()
            if (canvas != null) {
                val src = Rect(0, 0, w, h)
                val dst = Rect(0, 0, canvas.width, canvas.height)
                canvas.drawBitmap(mCacheBitmap, src, dst, paint)
            }
            holder.unlockCanvasAndPost(canvas)
            frameArray[0].release()
            frameArray.removeAt(0)
            Log.d(TAG, "Drawing")
        } else
            Log.d(TAG, "Empty")
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Thread(CameraRender()).start()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        threadRunning = false
    }

    inner class CameraRender : Runnable {
        override fun run() {
            threadRunning = true
            do {
                synchronized(this@CVView) {
                    try {
                        while (!frameReady && threadRunning)
                            (this@CVView as java.lang.Object).wait()

                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    if (frameReady) {
                        drawMat()
                        frameReady = false
                    }
                }
            } while (threadRunning)
        }

    }
}