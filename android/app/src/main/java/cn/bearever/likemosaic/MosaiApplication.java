package cn.bearever.likemosaic;

import android.util.Log;

import cn.bearever.mingbase.app.BaseApplication;
import io.agora.rtc.RtcEngine;

/**
 * @author luoming
 * @date 2020/4/16
 */
public class MosaiApplication extends BaseApplication {

    private AgoraEventHandler mHandler;
    private RtcEngine mRtcEngine;

    static {
        System.loadLibrary("apm-mosaic");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mHandler = new AgoraEventHandler();
            mRtcEngine = RtcEngine.create(getApplicationContext(), getString(R.string.agora_app_id), mHandler);
            Log.e("执行1", "----------");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("错误", e.toString());
        }
    }

    public void registerHandler(EventHandler handler){
        mHandler.addHandler(handler);
    }

    public void unregisterHandler(EventHandler handler){
        mHandler.removeHandler(handler);
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RtcEngine.destroy();
    }
}
