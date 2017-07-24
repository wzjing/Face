package com.wzjing.face.opencvcamera

import org.opencv.core.Mat

class Mat : Mat {
    constructor() : super()
    constructor(rows: Int, cols: Int, type: Int):super(rows, cols, type)
}