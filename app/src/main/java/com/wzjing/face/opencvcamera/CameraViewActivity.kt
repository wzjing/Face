package com.wzjing.face.opencvcamera

import android.graphics.PixelFormat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import com.wzjing.face.R
import kotlinx.android.synthetic.main.activity_cameraview.*
import org.jetbrains.anko.button
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.verticalLayout

class CameraViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cameraview)

        detectorSwitch.onCheckedChange { _, isChecked ->
            cameraView.enableFaceDetection = isChecked
        }

        detectorSwitch.isChecked = false

    }

    public inline fun ViewManager.cameraView() = cameraView{}
    public inline fun ViewManager.cameraView(init: CameraView.()->Unit) = ankoView({CameraView(it)}, R.style.AppTheme, init)

}
