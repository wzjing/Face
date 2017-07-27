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
import org.opencv.core.CvType
import org.opencv.imgproc.Imgproc

class CameraView(context: Context, var attrs: AttributeSet? = null) : SurfaceView(context, attrs), SurfaceHolder.Callback {
    private val TAG = "CameraView"

    private var camManager: CamManager = CamManager.Companion.Builder(context).build()
    public var frameListener: ((Mat) -> Mat)? = null
    private var mCacheBitmap: Bitmap? = null

    private val paint: Paint = Paint()


    init {
        mCacheBitmap = Bitmap.createBitmap(camManager.size.width, camManager.size.height, Bitmap.Config.RGB_565)
        camManager.previewListener = { w, h, data ->
            val bitmap = doAsyncResult {
                var mat = Mat(w, h, CvType.CV_8UC1)
                mat.put(w, h, data)
                mat = frameListener?.invoke(mat) ?: mat
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_YUV2BGR_NV21, 4)
                Utils.matToBitmap(mat, mCacheBitmap)
                mCacheBitmap
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

    companion object {
        init {
            System.loadLibrary("opencv_java3")
            System.loadLibrary("native-lib")
        }
    }

}