//
// Created by zhouy on 2020/6/1.
//

#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include <assert.h>

#include <vector>
#include <algorithm>

#include "agora/IAgoraMediaEngine.h"
#include "agora/IAgoraRtcEngine.h"




int _convertColor(int color, int size) {
    int step = 256 / size;
    color = 256 - color;
    color = 256 - color / step * step;
    if (color > 255) {
        return 255;
    }
    if (color < 0) {
        return 0;
    }
    return color;
}


class MediaObserver : public agora::media::IVideoFrameObserver {
public:
    virtual bool onCaptureVideoFrame(VideoFrame &videoFrame) override {

        __android_log_print(ANDROID_LOG_ERROR, "agoraencryption", "onCaptureVideoFrame");

         int height = videoFrame.height;
        int width = videoFrame.width;
        int scale = 32;
        int bit = 64;
        int len = width * height;

        //处理Y分量，分割为8个值
        int index, tindex, y;

        unsigned char *out_y = ((unsigned char*)videoFrame.yBuffer);
        unsigned char *out_u = ((unsigned char*)videoFrame.uBuffer);
        unsigned char *out_v = ((unsigned char*)videoFrame.vBuffer);

        char c2[10];
        sprintf(c2,"y=%d",((unsigned char*)videoFrame.yBuffer)[0]);
        __android_log_print(ANDROID_LOG_ERROR, "agoraencryption",c2);

        char c[100];

        sprintf(c,"onCaptureVideoFrame1 %d,%d,%d,%d,%d",height,width,scale,bit,len);

        __android_log_print(ANDROID_LOG_ERROR, "agoraencryption",c);

        for (int i = 0; i < height; i += scale) {
            for (int j = 0; j < width; j += scale) {
                index = width * i + j;
                y = out_y[index];

                for (int k = 0; k < scale; k++) {
                    for (int p = 0; p < scale; p++) {
                        tindex = index + (k * width + p);
                        if (tindex < len) {
                            y += out_y[tindex];
                        }
                    }
                }
                y = y / scale / scale;
                for (int k = 0; k < scale; k++) {
                    for (int p = 0; p < scale; p++) {
                        tindex = index + (k * width + p);
                        if (tindex < len) {
                            out_y[tindex] = _convertColor(y, bit);
                        }
                    }
                }
            }
        }

        __android_log_print(ANDROID_LOG_ERROR, "agoraencryption", "onCaptureVideoFrame2");

        //处理UV分量
        int u, v;
        index = tindex = u = v = 0;
        scale = scale / 2;
        for (int i = 0; i < height / 2; i += scale) {
            for (int j = 0; j < width / 2; j += scale) {
                index = width / 2 * i + j;
                u = v = 0;
                for (int k = 0; k < scale; k++) {
                    for (int p = 0; p < scale; p++) {
                        tindex = index + (k * width / 2 + p);
                        if (tindex < len / 4) {
                            u = u + out_u[tindex];
                            v = v + out_v[tindex];
                        }
                    }
                }
                u = u / scale / scale;
                v = v / scale / scale;

                for (int k = 0; k < scale; k++) {
                    for (int p = 0; p < scale; p++) {
                        tindex = index + (k * width / 2 + p);
                        if (tindex < len / 4) {
                            out_u[tindex] = u;
                            out_v[tindex] = v;
                        }
                    }
                }
            }
        }

        __android_log_print(ANDROID_LOG_ERROR, "agoraencryption", "onCaptureVideoFrame3");

        return true;
    }

    virtual bool onRenderVideoFrame(unsigned int uid, VideoFrame &videoFrame) override {
        __android_log_print(ANDROID_LOG_ERROR, "agoraencryption", "onRenderVideoFrame");
        return true;
    }

};

static MediaObserver s_mediaObserver;

static agora::rtc::IRtcEngine *rtcEngine = NULL;

static int sResult;

#ifdef __cplusplus
extern "C" {
#endif

int __attribute__((visibility("default")))
loadAgoraRtcEnginePlugin(agora::rtc::IRtcEngine *engine) {
    rtcEngine = engine;
    sResult += 1;
    // if do registerPacketObserver here, SDK may return -7(ERR_NOT_INITIALIZED)
    return 0;
}

void __attribute__((visibility("default")))
unloadAgoraRtcEnginePlugin(agora::rtc::IRtcEngine *engine) {
    rtcEngine = NULL;
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = -1;
    sResult += 2;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }
    assert(env != NULL);
    result = JNI_VERSION_1_6;
    return result;
}

JNIEXPORT void JNICALL
Java_cn_bearever_likemosaic_RtcPacketObserver_changeLevel(JNIEnv *env, jclass obj,int level) {
    if (!rtcEngine) {
        char c[30];
        sprintf(c, "还未初始化%d", sResult);
        __android_log_print(ANDROID_LOG_ERROR, "agoraencryption", c);
        return;
    }

    agora::util::AutoPtr<agora::media::IMediaEngine> mediaEngine;
    mediaEngine.queryInterface(rtcEngine, agora::AGORA_IID_MEDIA_ENGINE);
    if (mediaEngine) {
        //  if (enable) {
        mediaEngine->registerVideoFrameObserver(&s_mediaObserver);
        __android_log_print(ANDROID_LOG_ERROR, "agoraencryption", "注册成功了");
//        } else {
//            mediaEngine->registerVideoFrameObserver(NULL);
//        }
    }



}

JNIEXPORT void JNICALL
Java_cn_bearever_likemosaic_RtcPacketObserver_register(JNIEnv *env, jclass obj) {
    if (!rtcEngine) {
        char c[30];
        sprintf(c, "还未初始化%d", sResult);
        __android_log_print(ANDROID_LOG_ERROR, "agoraencryption", c);
        return;
    }

    agora::util::AutoPtr<agora::media::IMediaEngine> mediaEngine;
    mediaEngine.queryInterface(rtcEngine, agora::AGORA_IID_MEDIA_ENGINE);
    if (mediaEngine) {
        //  if (enable) {
        mediaEngine->registerVideoFrameObserver(&s_mediaObserver);
        __android_log_print(ANDROID_LOG_ERROR, "agoraencryption", "注册成功了");
//        } else {
//            mediaEngine->registerVideoFrameObserver(NULL);
//        }
    }



}

JNIEXPORT void JNICALL
Java_cn_bearever_likemosaic_RtcPacketObserver_unregister(JNIEnv *env, jclass obj) {
    if (!rtcEngine) {
        __android_log_print(ANDROID_LOG_ERROR, "agoraencryption", "还未初始化");
        return;
    }
    __android_log_print(ANDROID_LOG_ERROR, "agoraencryption", "doUnregisterProcessing");
    rtcEngine->registerPacketObserver(nullptr);
}

#ifdef __cplusplus
}
#endif