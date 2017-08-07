package com.wzjing.face.opencvcamera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.coroutines.experimental.*
import kotlin.ByteArray

class CameraView : SurfaceView, SurfaceHolder.Callback {
    private val TAG = "CameraView"

    private var camManager: CamManager = CamManager.Companion.Builder(context).build()
    private var mCacheBitmap: Bitmap
    private var currentIndex = 0

    private val paint: Paint = Paint()
    private var srcRect: Rect? = null
    private var dstRect: Rect? = null

    private var job: Job
    private var threadRunning = true
    private var frameReady = false
    private val dataList = arrayListOf<ByteArray>()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    var start: Long = System.currentTimeMillis()

    init {
        mCacheBitmap = Bitmap.createBitmap(camManager.size.width, camManager.size.height, Bitmap.Config.RGB_565)
        dataList.add(ByteArray((camManager.size.width * camManager.size.height * 1.5).toInt()))
        dataList.add(ByteArray((camManager.size.width * camManager.size.height * 1.5).toInt()))
        holder.addCallback(this)
        camManager.previewListener = { w, h, data ->
            dataList[currentIndex] = data
            frameReady = true
        }

        job = launch(newSingleThreadContext("nativeProcess")) {
            var hasFrame = false
            do {
                start = System.currentTimeMillis()
                synchronized(this@CameraView) {
                    if (frameReady) {
                        currentIndex = 1 - currentIndex
                        frameReady = false
                        hasFrame = true
                    }
                }
                if (threadRunning && hasFrame) {
                    if (dataList[1 - currentIndex].isNotEmpty())
                        drawFrame(dataList[1 - currentIndex])
                    hasFrame = false
                }

            } while (threadRunning)
        }
    }

    private fun drawFrame(data: ByteArray?) {
        Log.i(TAG, "drawFrame() start: ${System.currentTimeMillis() - start} ms")
        if (data == null || data.isEmpty())
            return
        nativeProcess(camManager.size.height, camManager.size.width, data.size, data, mCacheBitmap)
        Log.i(TAG, "drawFrame() nativeProcess Finished: ${System.currentTimeMillis() - start} ms")
        val canvas = holder.lockCanvas()
        assert(canvas == null) {
            if (canvas == null)
                Log.e(TAG, "Canvas is null")
            return
        }

        if (srcRect == null)
            srcRect = Rect(0, 0, mCacheBitmap.width, mCacheBitmap.height)
        if (dstRect == null) {
            val bw: Int = mCacheBitmap.width
            val bh: Int = mCacheBitmap.height
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
        canvas.drawBitmap(mCacheBitmap, srcRect, dstRect, paint)
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

    override fun surfaceDestroyed(holder: SurfaceHolder?) = runBlocking(CommonPool) {
        threadRunning = false
        job.join()
        mCacheBitmap.recycle()
        camManager.closeCamera()
    }

    private external fun nativeProcess(row: Int, col: Int, count: Int, data: ByteArray, bitmap: Bitmap)

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}