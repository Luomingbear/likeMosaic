package cn.bearever.likemosaic.call

import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import cn.bearever.likemosaic.Constant
import cn.bearever.likemosaic.R
import cn.bearever.likemosaic.bean.TopicBean
import cn.bearever.likemosaic.home.MosaicVideoSink
import cn.bearever.mingbase.app.mvp.BaseActivity
import com.jaeger.library.StatusBarUtil
import io.agora.rtc.RtcEngine
import io.agora.rtc.mediaio.IVideoSink
import kotlinx.android.synthetic.main.activity_video_chat_view.*
import java.util.*

/**
 * 视频聊天页面
 *
 * @author bear
 */
class VideoCallActivity : BaseActivity<VideoCallPresenter?>(), VideoCallContact.View {

    companion object {
        private val TAG = VideoCallActivity::class.java.simpleName
    }

    private var mMuted = false
    var mCallEnd = false
    private var mChannel = ""
    private var mToken = ""
    private var mRemoteView: View? = null
    private var mLocalView: View? = null

    private var mTopicList: ArrayList<TopicBean>? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_video_chat_view
    }

    override fun initData(saveInstanceState: Bundle?) {
        if (intent != null) {
            val intent = intent
            mChannel = intent.getStringExtra(Constant.KEY_CHANNEL)
            mToken = intent.getStringExtra(Constant.KEY_TOKEN)
            mTopicList = intent.getSerializableExtra(Constant.KEY_TOPIC_LIST) as ArrayList<TopicBean>
        }
        if (saveInstanceState != null) {
            mChannel = saveInstanceState.getString(Constant.KEY_CHANNEL)
            mToken = saveInstanceState.getString(Constant.KEY_TOKEN)
            mTopicList = saveInstanceState.getSerializable(Constant.KEY_TOPIC_LIST) as ArrayList<TopicBean>
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(Constant.KEY_TOKEN, mToken)
        outState.putString(Constant.KEY_CHANNEL, mChannel)
        outState.putSerializable(Constant.KEY_TOPIC_LIST, mTopicList)
        super.onSaveInstanceState(outState)
    }

    override fun initView() {
        StatusBarUtil.setTransparentForImageView(this, btn_mute)
        iv_like.clearAnimation()
        (iv_like.getDrawable() as AnimationDrawable).start()
        if (mTopicList != null) {
            refreshTags(mTopicList!!)
        }
        btn_back.setOnClickListener {
            endCall()
        }
    }

    override fun initPresenter() {
        mPresenter = VideoCallPresenter(this, this)
        startCall()
    }

    override fun refreshTags(topicList: List<TopicBean>) {
        for (i in (fl_tag.childCount - 1) downTo 0) {
            val child = fl_tag.getChildAt(i)
            if (child.id == R.id.btn_refresh || child.isSelected) {
                continue
            }

            fl_tag.removeView(child)
        }
        for (topic in topicList) {
            val view = createTopicView(topic)
            fl_tag.addView(view)
        }
    }

    private fun createTopicView(topic: TopicBean): View {
        val textView = TextView(this)
        textView.text = topic.text
        textView.setPadding(resources.getDimension(R.dimen.tag_padding_left).toInt(),
                resources.getDimension(R.dimen.tag_padding_top).toInt(),
                resources.getDimension(R.dimen.tag_padding_right).toInt(),
                resources.getDimension(R.dimen.tag_padding_bottom).toInt())
        textView.setBackgroundResource(R.drawable.drawable_tag)
        textView.setTextColor(Color.WHITE)
        textView.setTag(topic.id)
        return textView
    }

    override fun refreshLike(likeCount: Int) {
        // TODO: 2020/4/16
    }

    override fun onUserLeft() {
        runOnUiThread {
            removeRemoteVideo()
            endCall()
        }
    }

    override fun onUserJoin(uid: Int) {
        runOnUiThread { setupRemoteVideo(uid) }
    }

    private fun setupRemoteVideo(uid: Int) {
        val count = remote_video_view_container!!.childCount
        var view: View? = null
        for (i in 0 until count) {
            val v = remote_video_view_container!!.getChildAt(i)
            if (v.tag is Int && v.tag as Int == uid) {
                view = v
            }
        }
        if (view != null) {
            return
        }
        mRemoteView = MosaicVideoSink(this)
        remote_video_view_container!!.addView(mRemoteView)
        mPresenter?.setRemoteVideoRenderer(uid, (mRemoteView as IVideoSink?)!!)
        mRemoteView?.setTag(uid)
    }

    private fun removeRemoteVideo() {
        if (mRemoteView != null) {
            remote_video_view_container!!.removeView(mRemoteView)
        }
        mRemoteView = null
    }

    private fun setupLocalVideo() {
        mLocalView = MosaicVideoSink(this)
        local_video_view_container!!.addView(mLocalView)
        mPresenter!!.setLocalVideoRenderer((mLocalView as IVideoSink?)!!)
    }

    private fun joinChannel() {
        mPresenter!!.joinRoom(mChannel, mToken)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!mCallEnd) {
            leaveChannel()
        }
        RtcEngine.destroy()
    }

    private fun leaveChannel() {
        mPresenter?.quitRoom(mChannel)
        finish()
    }

    fun onLocalAudioMuteClicked(view: View?) {
        mMuted = !mMuted
        mPresenter!!.muteAudio(mMuted)
        val res = if (mMuted) R.drawable.btn_mute else R.drawable.btn_unmute
        btn_mute!!.setImageResource(res)
    }

    private fun startCall() {
        setupLocalVideo()
        joinChannel()
    }

    private fun endCall() {
        removeLocalVideo()
        removeRemoteVideo()
        leaveChannel()
    }

    private fun removeLocalVideo() {
        if (mLocalView != null) {
            local_video_view_container!!.removeView(mLocalView)
        }
        mLocalView = null
    }

    private fun showButtons(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        btn_mute!!.visibility = visibility
    }

    override fun onBackPressed() {
        //屏蔽返回操作，仅允许通过点击返回按钮退出房间
//        super.onBackPressed()
    }
}