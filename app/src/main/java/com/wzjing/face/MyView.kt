package com.wzjing.face

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

class MyView : SurfaceView, SurfaceHolder.Callback{

    var updating : Boolean
    var bitmap : Bitmap
    var src : Rect
    var bitmapPaint: Paint
    var linePaint : Paint
    val TAG = "MyView"
    var angle = 0f

    init {
        Log.i(TAG, "init")
        updating = false
        bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        src = Rect(0, 0, bitmap.width, bitmap.height)
        bitmapPaint = Paint()
        bitmapPaint.isFilterBitmap = true
        linePaint = Paint()
        linePaint.strokeWidth = 5f
        linePaint.color = Color.GREEN
        holder.addCallback(this)
    }

    constructor(context : Context) : super(context)

    constructor(context: Context, attrs : AttributeSet) : super(context, attrs)


    val update = Thread (Runnable{
        Log.i("MyView", "Start updating")
        updating = true
        while(updating) {
            draw()
            Thread.sleep(8)
            angle+=6

            Log.v("MyView", "Updating")
        }
    })

    fun draw(){
        val canvas = holder.lockCanvas()
        if (canvas != null) {
            canvas.drawColor(Color.WHITE)
            canvas.save()
            canvas.restore()
            canvas.rotate(angle, canvas.width / 2f, canvas.height / 2f)
            val dst = Rect(0, 0, canvas.width, canvas.height)
            canvas.drawBitmap(bitmap, src, dst, bitmapPaint)
            canvas.drawRect(300f,300f, 500f, 500f, linePaint)
            holder.unlockCanvasAndPost(canvas)
        }
    }


    override fun surfaceCreated(p0: SurfaceHolder?) {
        update.start()
    }


    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        updating = false
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
    }




}
