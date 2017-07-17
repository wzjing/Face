package com.wzjing.face

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.wzjing.face.customcamera.CameraActivity
import com.wzjing.face.opencvcamera.OpenCVCameraActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "MainAvtivity"
    private val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        videoRecBtn.setOnClickListener(this)
        audioRecBtn.setOnClickListener(this)

        val requestPermissions = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)
            requestPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED)
            requestPermissions.add(Manifest.permission.CAMERA)

        if (!requestPermissions.isEmpty())
            ActivityCompat.requestPermissions(this,
                    requestPermissions.toTypedArray(),
                    PERMISSION_REQUEST_CODE)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            PERMISSION_REQUEST_CODE ->
                    for (result in grantResults)  {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "No ${permissions[grantResults.indexOf(result)]} Permission", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
        }
    }
}