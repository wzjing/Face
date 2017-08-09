package com.wzjing.paint

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer : GLSurfaceView.Renderer {

    private var frame: Bitmap? = null
    private var mProgram: Int? = null
    private var mTexSampleHandle: Int? = null
    private var mTexCoordHandle: Int? = null
    private var mPosCoordHandle: Int? = null
    private var mTexVertices: FloatBuffer? = null
    private var mPosVertices: FloatBuffer? = null
    private val mTextures = IntArray(2)
    private var width = 0
    private var height = 0
    private var config: EGLConfig? = null

    public fun update(frame: Bitmap) {
        this.frame = frame
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val TEX_VERTICES = floatArrayOf( 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f)
        mTexVertices = ByteBuffer.allocateDirect(TEX_VERTICES.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTexVertices!!.put(TEX_VERTICES).position(0)
        val VERTEX_SHADER_CODE = "attribute vec4 a_position;\n" +
                "attribute vec2 a_texcoord;\n" +
                "varying vec2 v_texcoord;\n" +
                "void main() {\n" +
                "  gl_Position = a_position;\n" +
                "  v_texcoord = a_texcoord;\n" +
                "}\n"
        val FRAGMENT_SHADER_CODE = "precision mediup float;\n" +
                "uniform sampler2D tex_sampler;\n" +
                "varying vec2 v_texcoord;\n" +
                "void main() {\n" +
                "  gl_FragColor = texture2D(tex_sampler, v_texcoord);\n" +
                "}\n"
        mProgram =
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT and GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(mProgram!!)

    }

    companion object {

        public fun loadShader(type: Int, source: String): Int {
            var shader = GLES20.glCreateShader(type)
            if (shader != 0) {
                GLES20.glShaderSource(shader, source)
                GLES20.glCompileShader(shader)
                val compiled = intArrayOf(1)
                GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
                if (compiled[0] == 0) {
                    val info = GLES20.glGetShaderInfoLog(shader)
                    GLES20.glDeleteShader(shader)
                    shader = 0
                    throw RuntimeException("Could not compile shader: $type ($info)")
                }
            }
            return shader
        }

        public fun createProgram(vertexSourceCode: String, fragmentSourceCode: String):Int {
            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSourceCode)
            if (vertexShader == 0) return 0
            val pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSourceCode)
            if (pixelShader == 0) return 0
            val program = GLES20.glCreateProgram()
            if (program != 0) {
                GLES20.glAttachShader(program, vertexShader)

            }
        }

        public fun checkGLerror(op: String) {
            while (true) {
                val error = GLES20.glGetError()
                if (error != GLES20.GL_NO_ERROR) throw RuntimeException("$op: glError $error") else break
            }

        }
    }

}