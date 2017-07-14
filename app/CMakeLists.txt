cmake_minimum_required(VERSION 3.6.0)

add_library( native-lib
             SHARED
             src/main/cpp/native-lib.cpp )

find_library( log-lib
              log )

add_library( opencv_calib3d
             STATIC
             IMPORTED )
set_target_properties( opencv_calib3d
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_calib3d.a )

add_library( opencv_core
             STATIC
             IMPORTED )
set_target_properties( opencv_core
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_core.a )

add_library( opencv_features2d
             STATIC
             IMPORTED )
set_target_properties( opencv_features2d
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_features2d.a )

add_library( opencv_flann
             STATIC
             IMPORTED )
set_target_properties( opencv_flann
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_flann.a )

add_library( opencv_highgui
             STATIC
             IMPORTED )
set_target_properties( opencv_highgui
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_highgui.a )

add_library( opencv_imgcodecs
             STATIC
             IMPORTED )
set_target_properties( opencv_imgcodecs
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_imgcodecs.a )

add_library( opencv_imgproc
             STATIC
             IMPORTED )
set_target_properties( opencv_imgproc
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_imgproc.a )

add_library( opencv_java3
             SHARED
             IMPORTED )
set_target_properties( opencv_java3
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_java3.so )

add_library( opencv_ml
             STATIC
             IMPORTED )
set_target_properties( opencv_ml
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_ml.a )

add_library( opencv_objdetect
             STATIC
             IMPORTED )
set_target_properties( opencv_objdetect
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_objdetect.a )

add_library( opencv_photo
             STATIC
             IMPORTED )
set_target_properties( opencv_photo
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_photo.a )

add_library( opencv_shape
             STATIC
             IMPORTED )
set_target_properties( opencv_shape
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_shape.a )

add_library( opencv_stitching
             STATIC
             IMPORTED )
set_target_properties( opencv_stitching
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_stitching.a )

add_library( opencv_superres
             STATIC
             IMPORTED )
set_target_properties( opencv_superres
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_superres.a )

add_library( opencv_video
             STATIC
             IMPORTED )
set_target_properties( opencv_video
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_video.a )

add_library( opencv_videoio
             STATIC
             IMPORTED )
set_target_properties( opencv_videoio
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_videoio.a )

add_library( opencv_videostab
             STATIC
             IMPORTED )
set_target_properties( opencv_videostab
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_videostab.a )

include_directories( ${CMAKE_SOURCE_DIR}/src/libs/opencv/include )

target_link_libraries( native-lib
                       ${log-lib}
                       opencv_core
                       opencv_imgcodecs
                       opencv_imgproc
                       opencv_java3
                       opencv_objdetect)