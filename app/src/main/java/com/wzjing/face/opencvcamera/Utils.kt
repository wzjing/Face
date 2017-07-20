package com.wzjing.face.opencvcamera

import android.graphics.Bitmap
import org.opencv.android.Utils

class Utils() : Utils() {
    companion object {
        public fun matToBitmap(mat: Mat?, bitmap: Bitmap?) {
            Utils.matToBitmap(mat, bitmap)
        }
    }
}