package com.wzjing.face.opencvcamera

import android.content.Context
import android.graphics.*
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import com.wzjing.face.R
import com.wzjing.paint.MyRenderer
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLCameraView : GLSurfaceView {
    private val TAG = "CameraView"
    private val LCL = "LifeCycle"

    private var camManager: CamManager = CamManager.Companion.Builder(context).build()
    private var mCacheBitmap: Bitmap
    private var currentIndex = 0
    public var enableFaceDetection = false

    private val paint: Paint = Paint()
    private var srcRect: Rect? = null
    private var dstRect: Rect? = null

    private var job: Job? = null
    private var threadRunning = true
    private var frameReady = false
    private val dataList = arrayListOf<ByteArray>()

    // OpenGL ES
    private var renderer = Renderer()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var testBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.will)

    init {
        Log.d(LCL, "Init()")
        mCacheBitmap = Bitmap.createBitmap(camManager.size.height, camManager.size.width, Bitmap.Config.RGB_565)
        dataList.add(ByteArray((camManager.size.width * camManager.size.height * 1.5).toInt()))
        dataList.add(ByteArray((camManager.size.width * camManager.size.height * 1.5).toInt()))
        holder.addCallback(this)
        paint.isFilterBitmap = true
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    @Synchronized private fun glDrawFrame(data: ByteArray?) {
        val start = System.currentTimeMillis()
        Log.i(TAG, "drawFrame() start")
        if (data == null || data.isEmpty())
            return
        nativeProcess(camManager.size.height, camManager.size.width, data.size, data, mCacheBitmap, enableFaceDetection)
        Log.i(TAG, "drawFrame() nativeProcess: ${System.currentTimeMillis() - start} ms")
        Log.i(TAG, "drawFrame() finished: ${System.currentTimeMillis() - start} ms")
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        super.surfaceCreated(holder)
        Log.d(LCL, "surfaceCreated()")
        camManager.openCamera()
        camManager.previewListener = { _, _, data ->
            dataList[currentIndex] = data
            frameReady = true
        }

        job = launch(CommonPool) {
            var hasFrame = false
            do {
                synchronized(this@GLCameraView) {
                    if (frameReady) {
                        currentIndex = 1 - currentIndex
                        frameReady = false
                        hasFrame = true
                    }
                }
                if (threadRunning && hasFrame) {
                    if (dataList[1 - currentIndex].isNotEmpty())
                        glDrawFrame(dataList[1 - currentIndex])
                    hasFrame = false
                }

            } while (threadRunning)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, w: Int, h: Int) {
        super.surfaceChanged(holder, format, w, h)
        Log.i(LCL, "surfaceChanged()")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) = runBlocking(CommonPool) {
        super.surfaceDestroyed(holder)
        Log.i(LCL, "surfaceDestroyed()")
        threadRunning = false
        job?.join()
        mCacheBitmap.recycle()
        camManager.closeCamera()
        testBitmap.recycle()
    }

    inner class Renderer : GLSurfaceView.Renderer {
        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            initGLES(width, height)
        }

        override fun onDrawFrame(gl: GL10?) {
            step()
        }
    }

    private external fun nativeProcess(row: Int, col: Int, count: Int, data: ByteArray, bitmap: Bitmap, faceDetection: Boolean)
    private external fun initGLES(w: Int, h: Int)
    private external fun step()

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}