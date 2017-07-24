package com.wzjing.face.opencvcamera

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceView

class CameraView : SurfaceView{

    private var camManager: CamManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        CameraNew(context)
    else
        CameraOld(context)
    public var mFrameListener: OnFrameListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        TODO("Set the preview data call back here")
    }

    public interface OnFrameListener {
        fun onFrame(): Mat
    }

}