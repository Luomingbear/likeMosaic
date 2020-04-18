package cn.bearever.likemosaic.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luoming
 * @date 2020/4/12
 */
public class MatchResultBean extends BaseResultBean {
    /**
     * 实时消息的token
     */
    public String rtmToken;
    /**
     * 视频聊天的token
     */
    public String rtcToken;
    public String channel;
    public ArrayList<TopicBean> list;
}
