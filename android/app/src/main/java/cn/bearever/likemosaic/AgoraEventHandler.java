package cn.bearever.likemosaic;

import android.util.Log;

import java.util.ArrayList;

import io.agora.rtc.IRtcEngineEventHandler;

public class AgoraEventHandler extends IRtcEngineEventHandler {
    private ArrayList<EventHandler> mHandler = new ArrayList<>();

    public void addHandler(EventHandler handler) {
        mHandler.add(handler);
    }

    public void removeHandler(EventHandler handler) {
        mHandler.remove(handler);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        for (EventHandler handler : mHandler) {
            handler.onJoinChannelSuccess(channel, uid, elapsed);
        }
    }

    @Override
    public void onLeaveChannel(RtcStats stats) {
        for (EventHandler handler : mHandler) {
            handler.onLeaveChannel(stats);
        }
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {

        for (EventHandler handler : mHandler) {
            handler.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
        }
    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        for (EventHandler handler : mHandler) {
            handler.onRemoteVideoStateChanged(uid, state, reason, elapsed);
        }
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        for (EventHandler handler : mHandler) {
            handler.onUserJoined(uid, elapsed);
        }
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        for (EventHandler handler : mHandler) {
            handler.onUserOffline(uid, reason);
        }
    }

    @Override
    public void onLocalVideoStats(LocalVideoStats stats) {
        for (EventHandler handler : mHandler) {
            handler.onLocalVideoStats(stats);
        }
    }

    @Override
    public void onRtcStats(RtcStats stats) {
        for (EventHandler handler : mHandler) {
            handler.onRtcStats(stats);
        }
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        for (EventHandler handler : mHandler) {
            handler.onNetworkQuality(uid, txQuality, rxQuality);
        }
    }

    @Override
    public void onRemoteVideoStats(RemoteVideoStats stats) {
        for (EventHandler handler : mHandler) {
            handler.onRemoteVideoStats(stats);
        }
    }

    @Override
    public void onRemoteAudioStats(RemoteAudioStats stats) {
        for (EventHandler handler : mHandler) {
            handler.onRemoteAudioStats(stats);
        }
    }

    @Override
    public void onLastmileQuality(int quality) {
        for (EventHandler handler : mHandler) {
            handler.onLastmileQuality(quality);
        }
    }

    @Override
    public void onLastmileProbeResult(LastmileProbeResult result) {
        for (EventHandler handler : mHandler) {
            handler.onLastmileProbeResult(result);
        }
    }
}
