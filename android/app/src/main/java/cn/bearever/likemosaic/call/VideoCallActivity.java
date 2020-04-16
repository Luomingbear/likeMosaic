package cn.bearever.likemosaic.call;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bearever.likemosaic.Constant;
import cn.bearever.likemosaic.R;
import cn.bearever.likemosaic.bean.TopicBean;
import cn.bearever.likemosaic.home.MosaicVideoSink;
import cn.bearever.mingbase.app.mvp.BaseActivity;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.mediaio.IVideoSink;

/**
 * @author bear
 */
public class VideoCallActivity extends BaseActivity<VideoCallPresenter> implements VideoCallContact.View {
    private static final String TAG = VideoCallActivity.class.getSimpleName();
    private boolean mMuted;

    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;
    private SurfaceView mLocalView;
    private SurfaceView mRemoteView;

    private ImageView mLikeIv;
    private ImageView mMuteBtn;

    boolean mCallEnd = false;
    private String mChannel = "";
    private String mToken = "";
    private ArrayList<TopicBean> mTopicList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_chat_view;
    }

    @Override
    public void initData(Bundle saveInstanceState) {
        if (getIntent() != null) {
            Intent intent = getIntent();
            mChannel = intent.getStringExtra(Constant.KEY_CHANNEL);
            mToken = intent.getStringExtra(Constant.KEY_TOKEN);
            mTopicList = (ArrayList<TopicBean>) intent.getSerializableExtra(Constant.KEY_TOPIC_LIST);
        }
        if (saveInstanceState != null) {
            mChannel = saveInstanceState.getString(Constant.KEY_CHANNEL);
            mToken = saveInstanceState.getString(Constant.KEY_TOKEN);
            mTopicList = (ArrayList<TopicBean>) saveInstanceState.getSerializable(Constant.KEY_TOPIC_LIST);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constant.KEY_TOKEN, mToken);
        outState.putString(Constant.KEY_CHANNEL, mChannel);
        outState.putSerializable(Constant.KEY_TOPIC_LIST, mTopicList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void initView() {
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);

        mMuteBtn = findViewById(R.id.btn_mute);
        mLikeIv = findViewById(R.id.iv_like);
        mLikeIv.clearAnimation();
        ((AnimationDrawable) mLikeIv.getDrawable()).start();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new VideoCallPresenter(this, this);
        startCall();
    }


    @Override
    public void refreshTags(List<TopicBean> topicList) {
        // TODO: 2020/4/16
    }

    @Override
    public void refreshLike(int likeCount) {
        // TODO: 2020/4/16
    }

    @Override
    public void onUserLeft() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeRemoteVideo();
                endCall();
            }
        });
    }

    @Override
    public void onUserJoin(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupRemoteVideo(uid);
            }
        });
    }

    private void setupRemoteVideo(int uid) {
        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        int count = mRemoteContainer.getChildCount();
        View view = null;
        for (int i = 0; i < count; i++) {
            View v = mRemoteContainer.getChildAt(i);
            if (v.getTag() instanceof Integer && ((int) v.getTag()) == uid) {
                view = v;
            }
        }

        if (view != null) {
            return;
        }

        mRemoteView = new MosaicVideoSink(this);
        mRemoteContainer.addView(mRemoteView);
        mPresenter.setRemoteVideoRenderer(uid, (IVideoSink) mRemoteView);
        mRemoteView.setTag(uid);
    }

    private void removeRemoteVideo() {
        if (mRemoteView != null) {
            mRemoteContainer.removeView(mRemoteView);
        }
        mRemoteView = null;
    }


    private void setupLocalVideo() {
        mLocalView = new MosaicVideoSink(this);
        mLocalContainer.addView(mLocalView);
        mPresenter.setLocalVideoRenderer((IVideoSink) mLocalView);
    }

    private void joinChannel() {
        mPresenter.joinRoom(mChannel, mToken);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mCallEnd) {
            leaveChannel();
        }
        RtcEngine.destroy();
    }

    private void leaveChannel() {
        mPresenter.quitRoom(mChannel);
    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        mPresenter.muteAudio(mMuted);
        int res = mMuted ? R.drawable.btn_mute : R.drawable.btn_unmute;
        mMuteBtn.setImageResource(res);
    }

    private void startCall() {
        setupLocalVideo();
        joinChannel();
    }

    private void endCall() {
        removeLocalVideo();
        removeRemoteVideo();
        leaveChannel();
    }

    private void removeLocalVideo() {
        if (mLocalView != null) {
            mLocalContainer.removeView(mLocalView);
        }
        mLocalView = null;
    }

    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mMuteBtn.setVisibility(visibility);
    }
}
