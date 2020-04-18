package cn.bearever.likemosaic.call;

import android.nfc.Tag;

import java.util.List;

import cn.bearever.likemosaic.bean.TopicBean;
import cn.bearever.mingbase.app.mvp.IBaseModel;
import cn.bearever.mingbase.app.mvp.IBasePresenter;
import cn.bearever.mingbase.app.mvp.IBaseView;
import io.agora.rtc.mediaio.IVideoSink;

/**
 * @author luoming
 * @date 2020/4/16
 */
public class VideoCallContact {
    public interface View extends IBaseView {
        /**
         * 刷新话题区显示
         *
         * @param topicList
         */
        void refreshTags(List<TopicBean> topicList);

        /**
         * 更新好感度显示，需要更新马赛克级别和好感度进度条
         *
         * @param likeCount
         */
        void refreshLike(int likeCount);

        /**
         * 对方退出聊天
         */
        void onUserLeft();

        /**
         * 对方加入聊天
         *
         * @param uid
         */
        void onUserJoin(int uid);
    }

    public interface Model extends IBaseModel {
        /**
         * 登录
         *
         * @param token
         */
        void login(String token);

        /**
         * 退出登录
         */
        void logout();


        /**
         * 发送消息
         *
         * @param msg
         * @param uid
         */
        void sendMessage(String msg, String uid);
    }

    public interface Presenter extends IBasePresenter {

        /**
         * 设置本地视频渲染器
         *
         * @param sink
         */
        void setLocalVideoRenderer(IVideoSink sink);

        /**
         * 设置远程视频渲染器
         *
         * @param uid
         * @param sink
         */
        void setRemoteVideoRenderer(int uid, IVideoSink sink);

        /**
         * 加入房间
         *
         * @param channel
         * @param rtcToken
         * @param rtmToken
         */
        void joinRoom(String channel, String rtcToken, String rtmToken);

        /**
         * 离开房间
         *
         */
        void quitRoom();

        /**
         * 静音
         *
         * @param mute 是否静音
         */
        void muteAudio(boolean mute);

        /**
         * 发送信息
         *
         * @param msg
         * @param uid
         */
        void sendMessage(String msg, String uid);

        /**
         * 选中/取消选中 话题
         *
         * @param topicBean
         * @param isSelect
         */
        void selectTopic(TopicBean topicBean, boolean isSelect);

        /**
         * 发送喜欢信息，即点击屏幕
         */
        void sendLike();
    }
}
