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

class CameraView : SurfaceView, SurfaceHolder.Callback {
    private val TAG = "CameraView"

    private var camManager: CamManager = CamManager.Companion.Builder(context).build()
    private var mCacheBitmap: Bitmap? = null

    private val paint: Paint = Paint()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    init {
        mCacheBitmap = Bitmap.createBitmap(camManager.size.width, camManager.size.height, Bitmap.Config.ARGB_8888)
        camManager.previewListener = { w, h, data ->
            val bitmap = doAsyncResult {
                nativeProcess(w, h, data)
            }

            drawFrame(bitmap.get())

        }
        holder.addCallback(this)
    }

    private fun drawFrame(frame: Bitmap?) {
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
    }

    private external fun nativeProcess(w: Int, h: Int, data: ByteArray): Bitmap

}