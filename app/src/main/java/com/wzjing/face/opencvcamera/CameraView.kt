package com.wzjing.face.opencvcamera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import org.jetbrains.anko.doAsyncResult
import org.opencv.ml.LogisticRegression

class CameraView : SurfaceView, SurfaceHolder.Callback {
    private val TAG = "CameraView"

    private var camManager: CamManager = CamManager.Companion.Builder(context).build()
    private var mCacheBitmap: Bitmap? = null

    private val paint: Paint = Paint()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        camManager.previewListener = { w, h, data ->
            Log.i(TAG, "YUV data: $w x $h = ${data.size}")
            val bitmap = doAsyncResult {
                if (mCacheBitmap == null)
                    mCacheBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
                nativeProcess(h, w, data.size, data, mCacheBitmap!!)
                mCacheBitmap
            }
//            Log.d(TAG, "PreviewListener():")
//            if (mCacheBitmap == null)
//                mCacheBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
//            nativeProcess(w, h, data.size, data, mCacheBitmap!!)
            drawFrame(bitmap.get())

        }
        holder.addCallback(this)
    }

    private fun drawFrame(frame: Bitmap?) {
        Log.i(TAG, "Drawing frame: ${if(frame == null) "null" else "frame"}");
        val canvas = holder.lockCanvas()
        assert(canvas == null) {
            if (canvas == null)
                Log.e(TAG, "Canvas is null")
            return
        }
        canvas.drawBitmap(frame, Rect(0, 0, frame?.width ?: 0, frame?.height ?: 0), Rect(0, 0, canvas.width, canvas.height), paint)
        holder.unlockCanvasAndPost(canvas)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        camManager.openCamera()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        camManager.closeCamera()
        mCacheBitmap?.recycle()
        mCacheBitmap = null
    }

    private external fun nativeProcess(row: Int, col: Int,count: Int, data: ByteArray, bitmap: Bitmap)

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

}