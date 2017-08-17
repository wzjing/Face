#ifndef FACE_GLESVIEW_H
#define FACE_GLESVIEW_H

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include "native-util.h"

extern "C" {
JNIEXPORT void
JNICALL Java_com_wzjing_face_opencvcamera_GLESView_initGLES(JNIEnv *env, jobject obj, jint w,
                                                            jint h, jobject bitmap);

JNIEXPORT void
JNICALL Java_com_wzjing_face_opencvcamera_GLESView_step(JNIEnv *env, jobject);
}

inline void checkGlError(const char *op) {
    for (GLint error = glGetError(); error; error = glGetError()) {
        LOGE(TAG, "Operation: %s Error: 0x%x", op, error);
    }
}

inline void printGlString(const char *name, GLenum v) {
    const char *s = (const char *) glGetString(v);
    LOGI(TAG, "OpenGL ES: %s = %s", name, s);
}

typedef struct Frame {
    int w;
    int h;
    void *pixels;
} Frame;

static Frame frame;

GLuint loadShader(GLenum shaderType, const char *pSource);

GLuint createProgram(const char *pVertexSource, const char *pFragmentSource);

void initTextureParams();

bool setGraphics(JNIEnv *env, int w, int h, jobject bitmap);

void renderFrame();

static void update(int w, int h, void *pixels) {
    frame.w = w;
    frame.h = h;
    frame.pixels = pixels;
}

#endif //FACE_GLESVIEW_H
