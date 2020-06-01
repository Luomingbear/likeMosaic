package cn.bearever.likemosaic;

import android.util.Log;

public class RtcPacketObserver {

    static {
        Log.e("执行2","----------");
        System.loadLibrary("mosaic");
    }

    public final void registerProcessing() {
        register();
    }

    public final void unregisterProcessing() {
        unregister();
    }

    private native void register();

    private native void unregister();
}
