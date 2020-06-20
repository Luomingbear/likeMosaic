package io.agora.advancedvideo.videoencryption;

import android.util.Log;

public class PacketProcessor {

    static {
        Log.e("执行2","---------");
        System.loadLibrary("apm-plugin-packet-processing");

    }

    public final void registerProcessing() {
        doRegisterProcessing();
    }

    public final void unregisterProcessing() {
        doUnregisterProcessing();
    }

    private native void doRegisterProcessing();

    private native void doUnregisterProcessing();
}
