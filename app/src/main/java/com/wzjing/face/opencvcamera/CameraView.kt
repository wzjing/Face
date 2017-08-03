package com.wzjing.face.opencvcamera

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.doAsync

class CameraView : SurfaceView, SurfaceHolder.Callback {
    private val TAG = "CameraView"
    private val TAGV = "CameraViewView"

    private var camManager: CamManager = CamManager.Companion.Builder(context).build()
    private var mCacheBitmap = arrayListOf<Bitmap>()
    private var frameIndex = 0
    private var frameReady = false

    private val paint: Paint = Paint()
    private var srcRect: Rect? = null
    private var dstRect: Rect? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    var start: Double = System.currentTimeMillis().toDouble()

    init {
        mCacheBitmap.add(Bitmap.createBitmap(camManager.size.width, camManager.size.height, Bitmap.Config.RGB_565))

        holder.addCallback(this)
        camManager.previewListener = { w, h, data ->
            start = System.currentTimeMillis().toDouble()

            bg {
                frameReady = false
                val currentIndex = frameIndex
                val frame = bg {
                    nativeProcess(h, w, data.size, data, mCacheBitmap[currentIndex])
                    frameReady = true
                    mCacheBitmap[currentIndex]
                }
                frameIndex = 1 - frameIndex
                if (frameReady)
                    drawFrame(frame.getCompleted())
            }
            Log.i(TAG, "Frame time: ${System.currentTimeMillis() - start} ms")
        }
    }

    private fun drawFrame(frame: Bitmap?) {
        Log.i(TAG, "drawFrame() start: ${System.currentTimeMillis() - start} ms")
        val canvas = holder.lockCanvas()
        assert(canvas == null) {
            if (canvas == null)
                Log.e(TAG, "Canvas is null")
            return
        }

        if (srcRect == null)
            srcRect = Rect(0, 0, frame?.width ?: 0, frame?.height ?: 0)
        if (dstRect == null) {
            val bw: Int = frame?.width ?: 0
            val bh: Int = frame?.height ?: 0
            val dw: Int = canvas.height
            val dh = dw * bh / bw
            dstRect = Rect((canvas.width - dw) / 2,
                    (canvas.height - dh) / 2,
                    (canvas.width + dw) / 2,
                    (canvas.height + dh) / 2)
        }
        Log.i(TAG, "drawFrame() mark1: ${System.currentTimeMillis() - start} ms")
        canvas.drawColor(Color.BLACK)
        canvas.rotate(90f, canvas.width / 2f, canvas.height / 2f)
        Log.i(TAG, "drawFrame() mark2: ${System.currentTimeMillis() - start} ms")
        canvas.drawBitmap(frame, srcRect, dstRect, paint)
        Log.i(TAG, "drawFrame() mark3: ${System.currentTimeMillis() - start} ms")
        holder.unlockCanvasAndPost(canvas)
        Log.i(TAG, "drawFrame() finished: ${System.currentTimeMillis() - start} ms")
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        camManager.openCamera()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        Log.i(TAG, "Surface change")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        camManager.closeCamera()
        mCacheBitmap.forEach{
            it.recycle()
        }
    }

    private external fun nativeProcess(row: Int, col: Int, count: Int, data: ByteArray, bitmap: Bitmap)

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}