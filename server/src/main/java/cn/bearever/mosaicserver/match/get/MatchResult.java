package cn.bearever.mosaicserver.match.get;


import cn.bearever.mosaicserver.BaseResult;

/**
 * 匹配数据的返回值bean
 */
public class MatchResult extends BaseResult {
    /**
     * token
     */
    private String token = "";
    /**
     * 房间号
     */
    private String channel = "";

    public MatchResult() {
    }

    public MatchResult(String token, String channel) {
        this.token = token;
        this.channel = channel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
