package com.wzjing.face

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.wzjing.face.customcamera.CameraActivity
import com.wzjing.face.opencvcamera.OpenCVCameraActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "MainAvtivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        videoRecBtn.setOnClickListener(this)
        audioRecBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.videoRecBtn -> {
                val intent = Intent(this, CameraActivity::class.java);
                if (Build.VERSION.SDK_INT >= 21) {
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, v, getString(R.string.shareVideoImageElement))
                    startActivity(intent, options.toBundle())
                } else
                    startActivity(intent)
            }
            R.id.audioRecBtn -> {
                if (Build.VERSION.SDK_INT >= 21) {
                    val options = ActivityOptionsCompat.makeClipRevealAnimation(v, 500, 500, 0, 0)
                    startActivity(Intent(this, OpenCVCameraActivity::class.java), options.toBundle())
                } else {
                    startActivity(Intent(this, OpenCVCameraActivity::class.java))
                }
            }
        }
    }

    external fun stringFromJNI(): String

    companion object {

        init {
            System.loadLibrary("native-lib")
        }
    }
}