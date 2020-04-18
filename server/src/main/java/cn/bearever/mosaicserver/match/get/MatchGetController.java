package cn.bearever.mosaicserver.match.get;

import cn.bearever.mosaicserver.BaseResult;
import cn.bearever.mosaicserver.match.MatchManager;
import cn.bearever.mosaicserver.topic.TopicDao;
import cn.bearever.mosaicserver.topic.TopicRepository;
import cn.bearever.mosaicserver.topic.TopicService;
import io.agora.media.RtcTokenBuilder;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 匹配的接口
 */
@RestController
public class MatchGetController {
    private static String appId = "e6c014ba465c44dca97cb4352ea830fd";
    private static String appCertificate = "ccdf492d365b420592feeda2177d3e3e";
    private static int expirationTimeInSeconds = 3600;
    private Map<String, List<TopicDao>> topicMap = new HashMap<>();

    @Autowired
    private TopicService topicService;

    @GetMapping("/match/get")
    public BaseResult match(@RequestParam(value = "uid", defaultValue = "") String uid) {
        if (StringUtils.isEmpty(uid)) {
            BaseResult result = new BaseResult();
            result.setCode(BaseResult.CODE_FAILED);
            result.setMsg("uid不能为空");
            return result;
        }

        String channel = MatchManager.getInstance().getChannel(uid);
        if (channel == null) {
            BaseResult result = new BaseResult();
            result.setCode(BaseResult.CODE_FAILED);
            result.setMsg("还没有匹配上的频道");
            return result;
        } else {
            //生成token
            RtcTokenBuilder tokenBuilder = new RtcTokenBuilder();
            int timestamp = (int) (System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
            String token = tokenBuilder.buildTokenWithUid(appId, appCertificate, channel, Objects.hashCode(uid),
                    RtcTokenBuilder.Role.Role_Publisher, timestamp);
            //获取话题信息 todo 多线程同步问题
            List<TopicDao> list = null;
            if (topicMap.get(channel) != null) {
                list = topicMap.get(channel);
                topicMap.remove(channel);
            } else {
                list = topicService.getTopics(uid);
                topicMap.put(channel, list);
            }

            MatchResult result = new MatchResult();
            result.setMsg("频道获取成功");
            result.setToken(token);
            result.setChannel(channel);
            result.setList(list);
            return result;
        }
    }
}
