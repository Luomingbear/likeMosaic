package cn.bearever.likemosaic;

import android.util.Log;

public class RtcPacketObserver {

    public static native void register();

    public static native void unregister();
}
