package com.wzjing.face.opencvcamera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.os.Environment
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import org.opencv.core.CvType
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream

public class CVView : SurfaceView, SurfaceHolder.Callback {

    private val TAG = "CVView"

    private var mCacheBitmap: Bitmap? = null
    private val paint: Paint
    private var threadRunning = true
    private var frameReady = false
    private var frameArray: ArrayList<ByteArray>

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        paint = Paint()
        paint.isFilterBitmap = true
        frameArray = arrayListOf<ByteArray>()
        holder.addCallback(this)
        mCacheBitmap = Bitmap.createBitmap(1280, 720, Bitmap.Config.ARGB_8888)
    }

    public fun addFrame(width: Int, height:Int, frame: ByteArray) {
        if (frame.isNotEmpty()) {
            synchronized(this){
                frameArray.add(frame)
                frameReady = true
                (this@CVView as java.lang.Object).notify()
            }
        }
    }

    private lateinit var storeFile: File
    private fun openFile(): FileOutputStream{
        storeFile = File(Environment.getExternalStorageDirectory(), "frames.yuv")
        if (storeFile.exists())
            storeFile.delete()
        storeFile.createNewFile()
        return FileOutputStream(storeFile)
    }

    private fun writeMat(fos: FileOutputStream){
        fos.write(frameArray[0])
    }

    private fun drawMat() {
        if (frameArray.isNotEmpty()) {

            val mat = Mat(1080, 1280, CvType.CV_8UC1)
            mat.put(0, 0, frameArray[0])
            val rgb = Mat()
            Imgproc.cvtColor(mat, rgb, Imgproc.COLOR_YUV2RGBA_NV21, 4)
            Utils.matToBitmap(rgb, mCacheBitmap)
            val w = mCacheBitmap?.width ?: 0
            val h = mCacheBitmap?.height ?: 0
            val canvas = holder.lockCanvas()
            if (canvas != null && mCacheBitmap != null) {
                val src = Rect(0, 0, w, h)
                val dst = Rect(0, 0, canvas.width, canvas.height)
                canvas.drawBitmap(mCacheBitmap, src, dst, paint)
                Log.d(TAG, "Drawing")
            }
            holder.unlockCanvasAndPost(canvas)
            rgb.release()
            mat.release()
            frameArray.removeAt(0)
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
        mCacheBitmap?.recycle()
    }

    inner class CameraRender : Runnable {
        override fun run() {
            Log.d(TAG, "Thread Starting")
            threadRunning = true
            val fos = openFile()
            do {
                synchronized(this@CVView) {
                    try {
                        while (!frameReady && threadRunning){
                            Log.d(TAG, "Waiting{ frameReady:$frameReady, threadRunning:$threadRunning")
                            (this@CVView as java.lang.Object).wait()
                        }

                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    if (frameReady) {
                        Log.d(TAG, "Sending Mat")
                        writeMat(fos)
                        drawMat()
                        frameReady = false
                    }
                }
            } while (threadRunning)
            fos.flush()
            fos.close()
        }

    }
}