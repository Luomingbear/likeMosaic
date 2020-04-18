package cn.bearever.likemosaic.call

import android.content.Context
import android.util.Log
import cn.bearever.likemosaic.R
import cn.bearever.likemosaic.UidUtil
import cn.bearever.likemosaic.bean.MessageBean
import cn.bearever.likemosaic.bean.TopicBean
import cn.bearever.likemosaic.bean.TopicListResultBean
import cn.bearever.mingbase.BaseCallback
import cn.bearever.mingbase.app.mvp.BasePresenterIml
import cn.bearever.mingbase.app.util.ToastUtil
import cn.bearever.mingbase.chain.AsyncChain
import cn.bearever.mingbase.chain.core.AsyncChainRunnable
import cn.bearever.mingbase.chain.core.AsyncChainTask
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
    private var mChannel = ""

    init {
        initEngineAndJoinChannel()
    }

    private fun initEngineAndJoinChannel() {
        initializeEngine()
        setupVideoConfig()
    }


    private fun initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(context, context.getString(R.string.agora_app_id), object : IRtcEngineEventHandlerEx() {
                override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
                    Log.i(TAG, "onJoinChannelSuccess: ")
                }

                override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
                    getView()?.onUserJoin(uid)
                    Log.i(TAG, "onFirstRemoteVideoDecoded")
                }

                override fun onUserOffline(uid: Int, reason: Int) {
                    getView()?.onUserLeft()
                    Log.i(TAG, "onUserOffline")
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
        mModel?.registerMessage { message ->
            //接收到对方发送的消息
            if (message?.channel != mChannel) {
                return@registerMessage
            }

            AsyncChain.withMain(object : AsyncChainRunnable<Void, Void>() {
                override fun run(task: AsyncChainTask<Void, Void>?) {
                    ToastUtil.show(message.text)
                    task?.onComplete()
                }
            }).go()
        }
    }

    override fun setLocalVideoRenderer(sink: IVideoSink) {
        mRtcEngine?.setLocalVideoRenderer(sink)
    }

    override fun setRemoteVideoRenderer(uid: Int, sink: IVideoSink) {
        mRtcEngine?.setRemoteVideoRenderer(uid, sink)
    }

    override fun joinRoom(channel: String?, rtcToken: String?, rtmToken: String?, remoteUid: String?) {
        mChannel = channel ?: "";
        mModel?.loginRtm(rtmToken, channel, remoteUid)
        mRtcEngine?.joinChannel(rtcToken, channel, "", UidUtil.getUid(context).hashCode())
    }

    override fun quitRoom() {
        mRtcEngine?.leaveChannel()
        mModel?.logoutRtm()
    }

    override fun muteAudio(mute: Boolean) {
        mRtcEngine?.muteLocalAudioStream(mute)
    }

    override fun selectTopic(topicBean: TopicBean, isSelect: Boolean) {
        val message = MessageBean(mChannel)
        message.key = MessageBean.KEY_SELECT_TOPIC
        val map = HashMap<String, Any>()
        map["id"] = topicBean.id
        map["selected"] = isSelect
        message.data = map
        message.text = "选择" + topicBean.text
        mModel?.sendMessage(message)
    }

    override fun sendLike() {

    }

    override fun refreshTopics() {
        mModel?.getTopics(object : BaseCallback<TopicListResultBean>() {
            override fun suc(data: TopicListResultBean) {
                view?.refreshTags(data.list)
            }
        })
    }
}