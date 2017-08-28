#include "native-lib.h"
#include "native-utils.h"

Mat frame;
bool frameReady = false;

JNIEXPORT void
JNICALL
Java_com_wzjing_face_opencvcamera_GLCameraView_nativeProcess(JNIEnv *env, jobject instance,
                                                             jint row,
                                                             jint col, jint count, jbyteArray data_,
                                                             jobject bitmap,
                                                             jboolean faceDetection) {
    float start = clock();
    LOGI(TAG, "nativeProcess()-----------------------------------------------");
    char *data = (char *) env->GetPrimitiveArrayCritical(data_, 0);

    printPixels("origin", data);

    // Create Mat from origin YUV-NV21 data
    Mat yuvMat = Mat((int) (row * 1.5), col, CV_8UC1);

    memcpy(yuvMat.data, data, count * sizeof(char));
    env->ReleasePrimitiveArrayCritical(data_, data, JNI_ABORT);
    printPixels("Frame", yuvMat.data);
    cvtColor(yuvMat, frame, COLOR_YUV2RGB_NV21, 3);
    LOGI(TAG, "Step 1: %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);

    if (faceDetection)
        detectAndDraw(frame);
    frameReady = true;

    LOGI(TAG, "Finished: %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);
}

JNIEXPORT void
JNICALL Java_com_wzjing_face_opencvcamera_GLCameraView_initGLES(JNIEnv *env, jobject obj, jint w,
                                                                jint h, jobject bitmap) {
    setGraphics(env, w, h);
}

JNIEXPORT void
JNICALL Java_com_wzjing_face_opencvcamera_GLCameraView_step(JNIEnv *env, jobject obj) {
    renderFrame();
}

float size[2];

float vertexBuffer[8] = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};

GLint iChannel0;
GLint vertexCoord;
GLint uMatrix;
GLint iResolution;
GLint iGlobalTime;

float projectionMatrix[16];

GLuint texture;

GLuint gProgram;

bool setGraphics(JNIEnv *env, int w, int h) {

    printGlInfo();

    size[0] = w;
    size[1] = h;

    //Vertex Shader(Processing vertex position)
    const char *VERTEX_SHADER_CODE = loadAssetFile(env, "shaders/vertex_shader.glsl");
    //Fragment Shader(Processing pixels)
    const char *FRAGMENT_SHADER_CODE = loadAssetFile(env, "shaders/fragment_shader.glsl");

    LOGD(TAG, "setupGraphics(%d, %d)", w, h);
    gProgram = createProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
    if (!gProgram) {
        LOGE(TAG, "Could not create program.");
        return false;
    }

    iChannel0 = glGetUniformLocation(gProgram, "iChannel0");
    vertexCoord = glGetAttribLocation(gProgram, "vertexCoord");
    uMatrix = glGetUniformLocation(gProgram, "uMatrix");
    iResolution = glGetUniformLocation(gProgram, "iResolution");
    iGlobalTime = glGetUniformLocation(gProgram, "iGlobalTime");

    LOGD(TAG, "iChannel0:     %d", iChannel0);
    LOGD(TAG, "vertexCoordhandle:  %d", vertexCoord);
    LOGD(TAG, "uMatrix:       %d", uMatrix);
    LOGD(TAG, "iGlobalTime:   %d", iGlobalTime);

    glGenTextures(1, &texture);
    checkGlError("gen Textures");
    glBindTexture(GL_TEXTURE_2D, texture);
    checkGlError("bind Textures");
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, frame.cols, frame.rows, 0, GL_RGB, GL_UNSIGNED_BYTE,
                 frame.data);
    checkGlError("add a picture");

    //Init Texture parameters
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    checkGlError("set Texture Gravitys");

    LOGI(TAG, "Width: %d, Height: %d", w, h);
    glViewport(0, 0, w, h);
    checkGlError("glViewport");

    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    checkGlError("glClearColor");
    glClearStencil(0);
    checkGlError("glClearStencil");
    glClearDepthf(0);
    checkGlError("glClearDepthf");

    return true;
}

void renderFrame() {
    float start = clock();

    // Clear cache
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    checkGlError("glClear");
    glClearColor(1.0, 1.0, 1.0, 1.0);
    checkGlError("glClearColor");

    // Attach GLSL shader program
    glUseProgram(gProgram);
    checkGlError("glUseProgram");

    // Global Time
    glUniform1f(iGlobalTime, (float) clock() / CLOCKS_PER_SEC);
    checkGlError("set iGlobalTime");

    // OpenGL Resolution
    glUniform2f(iResolution, size[0], size[1]);
    checkGlError("set iResolution");

    // Projection Matrix
    glUniformMatrix4fv(uMatrix, 1, GL_FALSE, projectionMatrix);
    checkGlError("set uMatrix");

    // Vertex Handle
    glVertexAttribPointer(vertexCoord, 2, GL_FLOAT, GL_FALSE, 2 * sizeof(float), vertexBuffer);
    checkGlError("VertexBuffer");
    glEnableVertexAttribArray(vertexCoord);
    checkGlError("vertexHandle");

    // Texture Handle
//    glVertexAttribPointer(textureCoordHandle, 2, GL_FLOAT, GL_FALSE, 2* sizeof(GL_UNSIGNED_BYTE), textureBuffer);
//    checkGlError("textureBuffer");
//    glEnableVertexAttribArray(textureCoordHandle);
//    checkGlError("textureHandle");

    //Configure the texture data
    glActiveTexture(GL_TEXTURE0);
    checkGlError("activeTexture");
    glBindTexture(GL_TEXTURE_2D, texture);
    checkGlError("bind Texture");
    if (frameReady) {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, frame.cols, frame.rows, 0, GL_RGB,
                     GL_UNSIGNED_BYTE, frame.data);
        checkGlError("glTexImage2D");
        frameReady = false;
    }
    glUniform1i(iChannel0, 0);
    checkGlError("set iChannel0");

    //Draw the basic rect
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    checkGlError("glDrawArray");

    LOGI(TAG, "OpenGL ES frame: %.2f ms", (clock() - start) / CLOCKS_PER_MILLSEC);
}


