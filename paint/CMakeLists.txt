cmake_minimum_required(VERSION 3.6.0)

add_library( native-lib
             SHARED
             src/main/cpp/native-lib.cpp )

target_link_libraries( native-lib log android EGL GLESv2)