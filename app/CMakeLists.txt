cmake_minimum_required(VERSION 3.6.0)

add_library( native-lib
             SHARED
             src/main/cpp/native-lib.cpp )

find_library( log-lib
              log )

set(JNI_LIBS_DIR ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})

add_library( opencv_core STATIC IMPORTED )
set_target_properties( opencv_core PROPERTIES IMPORTED_LOCATION ${JNI_LIBS_DIR}/libopencv_core.a )

add_library( opencv_highgui STATIC IMPORTED )
set_target_properties( opencv_highgui PROPERTIES IMPORTED_LOCATION ${JNI_LIBS_DIR}/libopencv_highgui.a )

add_library( opencv_imgcodecs STATIC IMPORTED )
set_target_properties( opencv_imgcodecs PROPERTIES IMPORTED_LOCATION ${JNI_LIBS_DIR}/libopencv_imgcodecs.a )

add_library( opencv_imgproc STATIC IMPORTED )
set_target_properties( opencv_imgproc PROPERTIES IMPORTED_LOCATION ${JNI_LIBS_DIR}/libopencv_imgproc.a )

add_library( opencv_java3 SHARED IMPORTED )
set_target_properties( opencv_java3 PROPERTIES IMPORTED_LOCATION ${JNI_LIBS_DIR}/libopencv_java3.so )

add_library( opencv_objdetect STATIC IMPORTED )
set_target_properties( opencv_objdetect PROPERTIES IMPORTED_LOCATION ${JNI_LIBS_DIR}/libopencv_objdetect.a )

include_directories( ${CMAKE_SOURCE_DIR}/src/libs/opencv/include )

message("Android ABI: ${ANDROID_ABI}")

set(OPENCV_LIBS opencv_core opencv_highgui opencv_imgcodecs opencv_imgproc opencv_java3 opencv_objdetect)

# arm abi libs is diffrent from x86 abi libs, we need make diffrent configuration
if(ANDROID_ABI STREQUAL "armeabi-v7a" OR ANDROID_ABI STREQUAL "arm64-v8a")
    message("arm lib configuration")
    add_library( tegra_hal STATIC IMPORTED)
    set_target_properties( tegra_hal PROPERTIES IMPORTED_LOCATION ${JNI_LIBS_DIR}/libtegra_hal.a )
    set(OPENCV_LIBS ${OPENCV_LIBS} tegra_hal )
endif()

target_link_libraries( native-lib
                       ${log-lib}
                       ${OPENCV_LIBS}
                       jnigraphics )