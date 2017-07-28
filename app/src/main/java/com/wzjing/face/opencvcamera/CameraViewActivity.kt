package com.wzjing.face.opencvcamera

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewManager
import android.widget.RelativeLayout
import com.wzjing.face.R
import org.jetbrains.anko.button
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.verticalLayout

class CameraViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        relativeLayout {
            cameraView() {
                 gravity = Gravity.CENTER
            }
        }
    }

    public inline fun ViewManager.cameraView() = cameraView{}
    public inline fun ViewManager.cameraView(init: CameraView.()->Unit) = ankoView({CameraView(it)}, R.style.AppTheme, init)

}
