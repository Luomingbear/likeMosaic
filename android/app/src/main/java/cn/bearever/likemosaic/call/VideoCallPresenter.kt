package cn.bearever.likemosaic.call

import android.content.Context
import android.util.Log
import cn.bearever.likemosaic.R
import cn.bearever.likemosaic.UidUtil
import cn.bearever.likemosaic.bean.TopicBean
import cn.bearever.mingbase.app.mvp.BasePresenterIml
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.IRtcEngineEventHandlerEx
import io.agora.rtc.RtcEngine
import io.agora.rtc.mediaio.IVideoSink
import io.agora.rtc.video.VideoEncoderConfiguration

/**
 * @author luoming
 * @date 2020/4/16
 */
class VideoCallPresenter(view: VideoCallContact.View?, context: Context?) :
        BasePresenterIml<VideoCallContact.View?, VideoCallContact.Model?>(view, context), VideoCallContact.Presenter {

    companion object {
        private const val TAG = "VideoCallPresenter"
    }

    private var mRtcEngine: RtcEngine? = null

    init {
        initEngineAndJoinChannel()
    }

    private fun initEngineAndJoinChannel() {
        initializeEngine()
        setupVideoConfig()
    }

    /**
     * Event handler registered into RTC engine for RTC callbacks.
     * Note that UI operations needs to be in UI thread because RTC
     * engine deals with the events in a separate thread.
     */
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            Log.i(TAG, "onJoinChannelSuccess: ")
        }

        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            getView()?.onUserJoin(uid)
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            getView()?.onUserLeft()
        }
    }

    private fun initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(context, context.getString(R.string.agora_app_id), object : IRtcEngineEventHandlerEx() {
                override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
                    Log.i(TAG, "onJoinChannelSuccess: ")
                }

                override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
                    getView()?.onUserJoin(uid)
                    Log.i(TAG,"onFirstRemoteVideoDecoded")
                }

                override fun onUserOffline(uid: Int, reason: Int) {
                    getView()?.onUserLeft()
                    Log.i(TAG,"onUserOffline")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            throw RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e))
        }
    }

    private fun setupVideoConfig() {
        mRtcEngine?.enableVideo()
        mRtcEngine?.setVideoEncoderConfiguration(VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x480,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT))
    }

    override fun initModel() {
        mModel = VideoCallModel(context)
    }

    override fun setLocalVideoRenderer(sink: IVideoSink) {
        mRtcEngine?.setLocalVideoRenderer(sink)
    }

    override fun setRemoteVideoRenderer(uid: Int, sink: IVideoSink) {
        mRtcEngine?.setRemoteVideoRenderer(uid, sink)
    }

    override fun joinRoom(channel: String, token: String) {
        mModel?.login(token)
        mRtcEngine?.joinChannel(token, channel, "", UidUtil.getUid(context).hashCode())
    }

    override fun quitRoom(channel: String) {
        mRtcEngine?.leaveChannel()
    }
    override fun muteAudio(mute: Boolean) {
        mRtcEngine?.muteLocalAudioStream(mute)
    }

    override fun sendMessage(msg: String, uid: String) {

    }
    override fun selectTopic(topicBean: TopicBean, isSelect: Boolean) {

    }
    override fun sendLike() {

    }

}