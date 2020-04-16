package cn.bearever.likemosaic.call;

import android.content.Context;
import android.util.Log;

import cn.bearever.likemosaic.Constant;
import cn.bearever.likemosaic.MosaiApplication;
import cn.bearever.likemosaic.R;
import cn.bearever.likemosaic.UidUtil;
import cn.bearever.mingbase.app.store.SpManager;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmMessage;

/**
 * @author luoming
 * @date 2020/4/16
 */
public class VideoCallModel implements VideoCallContact.Model, RtmClientListener {
    private static final String TAG = "VideoCallModel";
    private Context mContext;
    private RtmClient mRtmClient;

    public VideoCallModel(Context context) {
        mContext = context.getApplicationContext();
        init(mContext);
    }

    @Override
    public void login(String token) {
        mRtmClient.login(token, UidUtil.getUid(MosaiApplication.getApplication()), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "onSuccess: ");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "onFailure: " + errorInfo.getErrorDescription());
            }
        });
    }

    @Override
    public void logout() {
        if (mRtmClient != null) {
            mRtmClient.logout(null);
        }
    }

    private void init(Context context) {
        try {
            mRtmClient = RtmClient.createInstance(context, context.getString(R.string.agora_app_id), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String msg, String uid) {
        if (mRtmClient == null) {
            init(mContext);
        }
        if (mRtmClient == null) {
            return;
        }

        RtmMessage message = mRtmClient.createMessage();
        message.setText(msg);
        mRtmClient.sendMessageToPeer(uid, message, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

    @Override
    public void onConnectionStateChanged(int i, int i1) {

    }

    @Override
    public void onMessageReceived(RtmMessage rtmMessage, String s) {

    }

    @Override
    public void onTokenExpired() {

    }
}
