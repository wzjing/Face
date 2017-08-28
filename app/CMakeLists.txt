cmake_minimum_required(VERSION 3.4.1)

add_library( native-lib
             SHARED
             src/main/cpp/native-lib.cpp
             src/main/cpp/native-utils.cpp
             src/main/cpp/opengles/gl-utils.cpp
             src/main/cpp/opencv/opencv-lib.cpp )

set( JNI_LIBS_DIR ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI} )

add_library( opencv_core STATIC IMPORTED )
set_target_properties( opencv_core PROPERTIES IMPORTED_LOCATION ${JNI_LIBS_DIR}/libopencv_core.a )

add_library( opencv_features2d STATIC IMPORTED )
set_target_properties( opencv_features2d PROPERTIES IMPORTED_LOCATION ${JNI_LIBS_DIR}/libopencv_features2d.a )

add_library( opencv_highgui STATIC IMPORTED )
set_target_properties( opencv_highgui PROPERTIES IMPORTED_LOCATION ${JNI_LIBS_DIR}/libopencv_highgui.a )

add_library( opencv_imgcodecs STATIC IMPORTED )
set_target_properties( opencv_imgcodecs PROPERTIES IMPORTED_LOCATION ${JNI_LIBS_DIR}/libopencv_imgcodecs.a )

add_library( opencv_imgproc STATIC IMPORTED )
set_target_properties( opencv_imgproc PROPERTIES IMPORTED_LOCATION ${JNI_LIBS_DIR}/libopencv_imgproc.a )

add_library( opencv_objdetect STATIC IMPORTED )
set_target_properties( opencv_objdetect PROPERTIES IMPORTED_LOCATION ${JNI_LIBS_DIR}/libopencv_objdetect.a )

add_library( tbb STATIC IMPORTED)
set_target_properties( tbb PROPERTIES IMPORTED_LOCATION ${JNI_LIBS_DIR}/libtbb.a )

add_library( tegra_hal STATIC IMPORTED)
set_target_properties( tegra_hal PROPERTIES IMPORTED_LOCATION ${JNI_LIBS_DIR}/libtegra_hal.a )

include_directories( ${CMAKE_SOURCE_DIR}/src/libs/opencv/include ${CMAKE_SOURCE_DIR}/src/main/cpp )

set( OPENCV_LIBS opencv_features2d opencv_objdetect opencv_imgproc opencv_highgui opencv_imgcodecs opencv_core tbb tegra_hal )

# arm abi libs is diffrent from x86 abi libs, we need make diffrent configuration
#if(ANDROID_ABI STREQUAL "armeabi-v7a" OR ANDROID_ABI STREQUAL "arm64-v8a")
#    message("arm lib configuration")
#    set(OPENCV_LIBS ${OPENCV_LIBS} tegra_hal )
#endif()

target_link_libraries( native-lib
                       ${OPENCV_LIBS}
                       android
                       log
                       jnigraphics
                       EGL
                       GLESv2
                       z )