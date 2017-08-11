package com.wzjing.face.opencvcamera

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewManager
import com.wzjing.face.R
import kotlinx.android.synthetic.main.activity_cameraview.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange

class CameraViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 16)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        setContentView(R.layout.activity_cameraview)

        detectorSwitch.onCheckedChange { _, isChecked ->
            cameraView.enableFaceDetection = isChecked
        }

        detectorSwitch.isChecked = false

    }

    public inline fun ViewManager.cameraView() = cameraView{}
    public inline fun ViewManager.cameraView(init: CameraView.()->Unit) = ankoView({CameraView(it)}, R.style.AppTheme, init)

}
