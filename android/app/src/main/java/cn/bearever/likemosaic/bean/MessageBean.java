package cn.bearever.likemosaic.bean;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 发送的消息格式
 *
 * @author luoming
 * @date 2020/4/18
 */
public class MessageBean implements Serializable {
    public String key;
    public HashMap<String, Object> data;
    public String text;
    /**
     * 当前频道信息
     */
    public String channel;

    public MessageBean(String channel) {
        this.channel = channel;
    }

    public static final String KEY_SELECT_TOPIC = "KEY_SELECT_TOPIC";
}
