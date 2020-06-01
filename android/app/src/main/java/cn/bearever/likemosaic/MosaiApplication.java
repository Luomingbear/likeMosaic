package cn.bearever.likemosaic;

import android.util.Log;

import cn.bearever.mingbase.app.BaseApplication;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.internal.RtcChannelImpl;

/**
 * @author luoming
 * @date 2020/4/16
 */
public class MosaiApplication extends BaseApplication {

    AgoraEventHandler handler;
    private RtcEngine mRtcEngine;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            handler = new AgoraEventHandler();
            mRtcEngine = RtcEngine.create(getApplicationContext(), getString(R.string.agora_app_id), handler);
            Log.e("执行1", "----------");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("错误", e.toString());
        }
        new RtcPacketObserver().registerProcessing();
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }
}
