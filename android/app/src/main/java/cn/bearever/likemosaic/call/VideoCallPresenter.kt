package cn.bearever.likemosaic.call

import android.content.Context
import android.text.TextUtils
import android.util.Log
import cn.bearever.likemosaic.R
import cn.bearever.likemosaic.UidUtil
import cn.bearever.likemosaic.bean.MessageBean
import cn.bearever.likemosaic.bean.SelectTopicBean
import cn.bearever.likemosaic.bean.TopicBean
import cn.bearever.likemosaic.bean.TopicListResultBean
import cn.bearever.mingbase.BaseCallback
import cn.bearever.mingbase.app.mvp.BasePresenterIml
import cn.bearever.mingbase.app.util.ToastUtil
import io.agora.rtc.IRtcEngineEventHandlerEx
import io.agora.rtc.RtcEngine
import io.agora.rtc.mediaio.IVideoSink
import io.agora.rtc.video.VideoEncoderConfiguration
import java.util.*
import kotlin.collections.ArrayList

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
    //我对对方的好感度
    private var mLikeCountMe2Other = 60
    //对方对我的好感度
    private var mLikeCountOther2Me = 60
    private var mTimerCount = 0
    private val LOCK_LIKE_COUNT = Any()
    private lateinit var mTimer: Timer

    init {
        initEngineAndJoinChannel()
        initLikeTimer()
    }

    private fun initLikeTimer() {
        //每一秒钟将mLikeCount-1
        mTimer = Timer()
        mTimer.schedule(object : TimerTask() {
            override fun run() {
                synchronized(LOCK_LIKE_COUNT) {
                    mLikeCountMe2Other--
                    mLikeCountOther2Me--
                }

                view?.refreshLike(mLikeCountOther2Me)

                if (mLikeCountMe2Other <= 0) {
                    //好感度为0，聊天结束
                    view?.localLikeEmpty()
                    mTimer.cancel()
                    return
                }
                if (mLikeCountOther2Me <= 0) {
                    view?.onUserLeft()
                    mTimer.cancel()
                }

                mTimerCount++
                if (mTimerCount == 10) {
                    view?.showQuitBtn()
                }
                LikeManager.getInstance().setLikeCountMe2Other(mLikeCountMe2Other)
                LikeManager.getInstance().setLikeCountOther2Me(mLikeCountOther2Me)
                sendLike()
            }
        }, 1000, 1000)
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

            when (message.key) {
                MessageBean.KEY_SELECT_TOPIC -> {
                    //选择/取消选择话题
                    view?.receiveSelectTag(message.data as SelectTopicBean?)
                }

                MessageBean.KEY_REFRESH_TOPIC -> {
                    view?.refreshTags(message.data as ArrayList<TopicBean>)
                    view?.startRefreshAnimation(false)
                }

                MessageBean.KEY_REMOTE_LIKE_CHANGE -> {
                    mLikeCountOther2Me = message.data as Int
                    view?.refreshLike(mLikeCountOther2Me)
                }
            }
            if (!TextUtils.isEmpty(message.text)) {
                ToastUtil.show(message.text)
            }
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
        mTimer.cancel()
    }

    override fun muteAudio(mute: Boolean) {
        mRtcEngine?.muteLocalAudioStream(mute)
    }

    override fun selectTopic(topicBean: TopicBean, isSelect: Boolean) {
        val message = MessageBean<SelectTopicBean>(mChannel)
        message.key = MessageBean.KEY_SELECT_TOPIC
        val selectTopicBean = SelectTopicBean()
        selectTopicBean.id = topicBean.id
        selectTopicBean.selected = isSelect
        message.data = selectTopicBean
        if (isSelect) {
            message.text = "对方选择了【" + topicBean.text + "】话题"
        } else {
            message.text = "对方取消了【" + topicBean.text + "】话题"
        }
        mModel?.sendMessage(message)
    }

    override fun addLike() {
        synchronized(LOCK_LIKE_COUNT) {
            mLikeCountMe2Other++
        }
    }

    private fun sendLike() {
        val message = MessageBean<Int>(mChannel)
        message.key = MessageBean.KEY_REMOTE_LIKE_CHANGE
        message.data = mLikeCountMe2Other
        mModel?.sendMessage(message)
    }

    override fun refreshTopics() {
        mModel?.getTopics(object : BaseCallback<TopicListResultBean>() {
            override fun suc(data: TopicListResultBean) {
                view?.refreshTags(data.list)
                val message = MessageBean<ArrayList<TopicBean>>(mChannel)
                message.key = MessageBean.KEY_REFRESH_TOPIC
                message.data = data.list
                message.text = "对方刷新了话题区"
                mModel?.sendMessage(message)
            }
        })
    }
}