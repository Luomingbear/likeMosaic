#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include <assert.h>

#include <vector>
#include <algorithm>

#include "./include/agora/IAgoraMediaEngine.h"
#include "./include/agora/IAgoraRtcEngine.h"

#include "./include/packet_processing_plugin_jni.h"

class AgoraRTCPacketObserver : public agora::rtc::IPacketObserver {
    typedef std::vector<unsigned char> buffer_type;

public:
    AgoraRTCPacketObserver() {
        m_txAudioBuffer.resize(2048);
        m_rxAudioBuffer.resize(2048);
        m_txVideoBuffer.resize(2048);
        m_rxVideoBuffer.resize(2048);
    }

    virtual bool onSendAudioPacket(Packet &packet) override {
        return onSendPacket(packet, m_txAudioBuffer);
    }

    virtual bool onSendVideoPacket(Packet &packet) override {
        return onSendPacket(packet, m_txVideoBuffer);
    }

    virtual bool onReceiveAudioPacket(Packet &packet) override {
        return onReceivePacket(packet, m_rxAudioBuffer);
    }

    virtual bool onReceiveVideoPacket(Packet &packet) override {
        return onReceivePacket(packet, m_rxVideoBuffer);
    }

private:
    static bool onSendPacket(Packet &packet, buffer_type &buffer) {
        //__android_log_print(ANDROID_LOG_INFO, "agoraencryption", "onSendPacket");
        size_t i;
        const unsigned char *p = packet.buffer;
        const unsigned char *pe = packet.buffer + packet.size;
        for (i = 0; p < pe && i < buffer.size(); ++p, ++i) {
            buffer[i] = *p ^ 0x55;
        }
        packet.buffer = &buffer[0];
        packet.size = i;
        return true;
    }

    static bool onReceivePacket(Packet &packet, buffer_type &buffer) {
        //__android_log_print(ANDROID_LOG_INFO, "agoraencryption", "onReceivePacket");
        size_t i = 0;
        const unsigned char *p = packet.buffer;
        const unsigned char *pe = packet.buffer + packet.size;
        for (i = 0; p < pe && i < buffer.size(); ++p, ++i) {
            buffer[i] = *p ^ 0x55;
        }
        packet.buffer = &buffer[0];
        packet.size = i;
        return true;
    }

private:
    buffer_type m_txAudioBuffer;
    buffer_type m_rxAudioBuffer;
    buffer_type m_txVideoBuffer;
    buffer_type m_rxVideoBuffer;
};

static AgoraRTCPacketObserver s_packetObserver;

static agora::rtc::IRtcEngine *rtcEngine = NULL;

static int sResult = 0;

#ifdef __cplusplus
extern "C" {
#endif

int __attribute__((visibility("default")))
loadAgoraRtcEnginePlugin(agora::rtc::IRtcEngine *engine) {
    rtcEngine = engine;
    sResult = 1;
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
    sResult = 2;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }

    assert(env != NULL);
    result = JNI_VERSION_1_6;
    return result;
}

JNIEXPORT void JNICALL
Java_io_agora_advancedvideo_videoencryption_PacketProcessor_doRegisterProcessing
        (JNIEnv *env, jobject obj) {
    if (!rtcEngine) {
        __android_log_print(ANDROID_LOG_INFO, "agoraencryption", "还未初始化");
        return;
    }
    char c[30];
    sprintf(c, "已经初始化啦初始化%d", sResult);

    __android_log_print(ANDROID_LOG_INFO, "agoraencryption",c);

    rtcEngine->registerPacketObserver(&s_packetObserver);
}

JNIEXPORT void JNICALL
Java_io_agora_advancedvideo_videoencryption_PacketProcessor_doUnregisterProcessing
        (JNIEnv *env, jobject obj) {
    if (!rtcEngine) {
        return;
    }
    __android_log_print(ANDROID_LOG_INFO, "agoraencryption", "doUnregisterProcessing");
    rtcEngine->registerPacketObserver(nullptr);
}

#ifdef __cplusplus
}
#endif
