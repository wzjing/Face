@file:Suppress("DEPRECATION")

package com.wzjing.face.customcamera

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Point
import android.hardware.Camera
import android.opengl.GLSurfaceView
import android.os.Environment
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MyGLSurfaceView : GLSurfaceView {

    /* Constructor */
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    /* Init */
    val TAG = "MyGLSurfaceView"
    var mContext: Context
    var mHolder: SurfaceHolder
    lateinit var cam: Camera
    var screenSize: Point

    init {
        Log.d(TAG, "init")
        mContext = context
        mHolder = holder
        mHolder.addCallback(this)
        screenSize = Point(mContext.resources.displayMetrics.widthPixels, mContext.resources.displayMetrics.heightPixels)

        setRenderer(MyRenderer())
    }

    /* Callback2 override */
    override fun surfaceCreated(holder: SurfaceHolder?) {
        super.surfaceCreated(holder)
        Log.d(TAG, "Surface created, holder is ${holder == null}")
        openCamera(holder)

    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, w: Int, h: Int) {
        super.surfaceChanged(holder, format, w, h)
        Log.d(TAG, "Surface changed")

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        super.surfaceDestroyed(holder)
        Log.d(TAG, "Surface destroyed")
        shooting = false
        releaseCamera()

    }

    /* Shooting control */
    var shooting = false
    var currentFrame = 1
    var maxFrame = 125
    fun startShoot() {
        shooting = true

        writingThread.start()
        Log.i(TAG, "start shooting")
    }

    fun stopShoot() {
        shooting = false
        Log.i(TAG, "stop shooting")
    }

    fun openFile(): OutputStream {
        val parent = File(Environment.getExternalStorageDirectory(), "/yuv")
        if (!parent.exists())
            parent.mkdirs()
        val rawFile = File(parent, "frames.yuv")
        if (rawFile.exists())
            rawFile.delete()
        if (rawFile.createNewFile()) return FileOutputStream(rawFile) else throw RuntimeException("Unable to create file")
    }

    /* data output */
    fun writeBytes(bytes: ByteArray) {
        Log.d(TAG, "Post to writting thread (${bytes.size})")
        frames.add(bytes)
    }

    var frames = arrayListOf<ByteArray>()
    val writingThread = Thread(Runnable {
        val fis = openFile()
        while (shooting) {
            if (frames.size == 0 || frames[0].isEmpty())
                continue
            Log.i(TAG, "Writing bytes(${frames[0].size})")
            fis.write(frames[0])
            frames.removeAt(0)
            Log.d(TAG, "frame $currentFrame wrote")
            currentFrame++
        }
        fis.flush()
        fis.close()
        stopShoot()
    })


    private fun openCamera(holder: SurfaceHolder?) {
        cam = Camera.open()
        val params = cam.parameters
        params.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        val size = getBestCameraResolution(params, 960)
        params.setPreviewSize(size.width, size.height)
        params.previewFormat = ImageFormat.YV12
        params.previewFrameRate = 25
        cam.parameters = params
//        assert(holder == null) {
//            Log.i(TAG, "Null")
//            return
//        }
        cam.setPreviewDisplay(holder)

        cam.setPreviewCallback { data, _ ->
            //            Log.d(TAG, "shooting:$shooting, data:${data.size}, currentFrame:$currentFrame");
            if (shooting && data != null && currentFrame <= maxFrame)
                writeBytes(data)
        }
        cam.setDisplayOrientation(90)
        cam.startPreview()
    }

    private fun releaseCamera() {
        Log.i(TAG, "Release camera")
        cam.stopPreview()
        cam.setPreviewDisplay(null)
        cam.setPreviewCallback(null)
        cam.release()
    }


    /**
     * @param parameters    Camera Parameters
     * @param width         The video width of desire(in original camera direction)
     * @return              The best choice of Camera.Size
     */
    private fun getBestCameraResolution(parameters: Camera.Parameters, width: Int): Camera.Size {

        for (x in parameters.supportedPreviewSizes) {
            Log.i(TAG, "Preview Size : ${x.width} x ${x.height}")
        }

        for (x in parameters.supportedPreviewFpsRange) {
            Log.i(TAG, "Preview fps rage : [${x[0]/1000}, ${x[1]/1000}]")
        }

        for (x in parameters.supportedVideoSizes) {
            Log.i(TAG, "Video Size : ${x.width} x ${x.height}")
        }

        val videoSizes = parameters.supportedPreviewSizes

        val key = videoSizes.find {
            val index = videoSizes.indexOf(it)
            Math.abs(it.width - width) <= Math.abs(videoSizes[if (index + 1 > videoSizes.size - 1) index else index + 1].width - width) &&
                    Math.abs(it.width - width) <= Math.abs(videoSizes[if (index - 1 < 0) index else index - 1].width - width)
        }
        Log.i(TAG, "The best fit is ${videoSizes.indexOf(key)}")
        return key ?: videoSizes[videoSizes.size - 1]
    }
}