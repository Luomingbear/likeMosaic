cmake_minimum_required(VERSION 2.8)

add_library(
        apm-mosaic
        SHARED
        src/main/jni/mosaic.cpp
        src/main/jni/agoraObserver.cpp)

find_library(log-lib log)

target_link_libraries(apm-mosaic ${log-lib})