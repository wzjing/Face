@file:Suppress("DEPRECATION")

package com.wzjing.face.customcamera

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Point
import android.hardware.Camera
import android.os.Environment
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MyGLSurfaceView : SurfaceView, SurfaceHolder.Callback {

    /* Constructor */
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    /* Init */
    private val TAG = "MyGLSurfaceView"
    private var mContext: Context
    private var mHolder: SurfaceHolder
    private lateinit var cam: Camera
    private var screenSize: Point

    init {
        Log.d(TAG, "init")
        mContext = context
        mHolder = holder
        mHolder.addCallback(this)
        screenSize = Point(mContext.resources.displayMetrics.widthPixels, mContext.resources.displayMetrics.heightPixels)
    }

    /* Callback2 override */
    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.d(TAG, "Surface created, holder is ${holder == null}")
        openCamera(holder)

    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, w: Int, h: Int) {
        Log.d(TAG, "Surface changed")

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        Log.d(TAG, "Surface destroyed")
        shooting = false
        releaseCamera()

    }

    /* Shooting control */
    private var shooting = false
    private var currentFrame = 1
    private var maxFrame = 125
    fun startShoot() {
        shooting = true

        writingThread.start()
        Log.i(TAG, "start shooting")
    }

    private fun stopShoot() {
        shooting = false
        Log.i(TAG, "stop shooting")
    }

    private fun openFile(): OutputStream {
        val parent = File(Environment.getExternalStorageDirectory(), "/yuv")
        if (!parent.exists())
            parent.mkdirs()
        val rawFile = File(parent, "frames.yuv")
        if (rawFile.exists())
            rawFile.delete()
        if (rawFile.createNewFile()) return FileOutputStream(rawFile) else throw RuntimeException("Unable to create file")
    }

    /* data output */
    private fun writeBytes(bytes: ByteArray) {
        Log.d(TAG, "Post to writting thread (${bytes.size})")
        frames.add(bytes)
    }

    private val frames = arrayListOf<ByteArray>()
    private val writingThread = Thread(Runnable {
        val fis = openFile()
        while (shooting) {
            if (frames.isEmpty() || frames[0].isEmpty())
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
        val size = getBestCameraResolution(params, 1280)
        params.setPreviewSize(size.width, size.height)
        params.previewFormat = ImageFormat.YV12
        params.previewFrameRate = 30
        cam.parameters = params

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