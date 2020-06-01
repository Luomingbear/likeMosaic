cmake_minimum_required(VERSION 2.8)

add_library(
        mosaic
        SHARED
        src/main/jni/mosaic.cpp
        src/main/jni/agoraObserver.cpp)

find_library(log-lib log)

target_link_libraries(mosaic ${log-lib})