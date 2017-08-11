#ifndef FACE_NATIVE_LIB_H
#define FACE_NATIVE_LIB_H

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>

#define TAG "native-log"
#define LOGD(tag, format, ...) __android_log_print(ANDROID_LOG_DEBUG, tag, format, ## __VA_ARGS__)
#define LOGI(tag, format, ...) __android_log_print(ANDROID_LOG_INFO, tag, format, ## __VA_ARGS__)
#define LOGE(tag, format, ...) __android_log_print(ANDROID_LOG_ERROR, tag, format, ## __VA_ARGS__)

extern "C" {
JNIEXPORT void
JNICALL Java_com_wzjing_paint_GLESView_initGLES(JNIEnv * env, jobject obj, jint w, jint h);

JNIEXPORT void
JNICALL Java_com_wzjing_paint_GLESView_step(JNIEnv * env, jobject);
}

inline void checkGlError(const char* op) {
    for (GLint error = glGetError(); error ; error = glGetError()) {
        LOGE(TAG, "Operation: %s Error: 0x%x", op, error);
    }
}

inline void printGlString(const char* name, GLenum v) {
    const char* s = (const char *) glGetString(v);
    LOGI(TAG, "OpenGL ES: %s = %s", name, s);
}

auto VERTEX_SHADER_CODE =
        "attribute vec4 a_position;\n"
        "attribute vec2 a_texcoord;\n"
        "varying vec2 v_texcoord;\n"
        "void main() {\n"
        "  gl_Position = a_position;\n"
        "  v_texcoord = a_texcoord;\n"
        "}\n";
auto FRAGMENT_SHADER_CODE =
        "precision mediump float;\n"
        "uniform sampler2D tex_sampler;\n"
        "varying vec2 v_texcoord;\n"
        "void main() {\n"
        "  gl_FragColor = texture2D(tex_sampler, v_texcoord);\n"
        "}\n";

GLuint loadShader(GLenum shaderType, const char* pSource);

GLuint createProgram(const char* pVertexSource, const char* pFragmentSource);

bool setGraphics(int w, int h);

void renderFrame();

#endif
