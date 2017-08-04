package com.wzjing.paint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import org.jetbrains.anko.coroutines.experimental.bg

class MySurfaceView(context: Context): SurfaceView(context), SurfaceHolder.Callback {

    private val TAG = "MySurfaceView"

    private var bitmap: Bitmap? = null
    private var rending = false;

    init {
        holder.addCallback(this)
    }

    private fun drawFarme() {
        val canvas = holder.lockCanvas()
        if (canvas == null)
            return
        val start: Long = System.currentTimeMillis()
        canvas.drawBitmap(bitmap!!, Rect(0, 0, bitmap?.width?:0, bitmap?.height?:0), Rect(0, 0, canvas.width, canvas.height), null)
        Log.i(TAG, "Frame time: ${System.currentTimeMillis()-start}")
        holder.unlockCanvasAndPost(canvas)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.sample_720)
        rending = true

//        Thread(Runnable {
//            while(rending)
//                drawFarme()
//        } ).start()

        bg {
            while(rending)
                drawFarme()
        }

    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        rending = false
        bitmap?.recycle()
    }

}
