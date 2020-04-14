package cn.bearever.likemosaic.home;

import android.content.Context;

import cn.bearever.likemosaic.bean.BaseResultBean;
import cn.bearever.likemosaic.bean.MatchResultBean;
import cn.bearever.mingbase.BaseCallback;

/**
 * @author luoming
 * @date 2020/4/12
 */
public class HomeModel implements HomeContact.Model {
    private Context context;

    public HomeModel(Context context) {
        this.context = context;
    }

    @Override
    public void postMatch(String uid, BaseCallback callback) {
        //todo 请求匹配接口
        if (callback != null) {
            callback.suc(new BaseResultBean());
        }
    }

    @Override
    public void getMatchState(String uid, BaseCallback<MatchResultBean> callback) {
        //todo 获取匹配状态接口，这里需要使用轮询
        if (callback != null) {
            MatchResultBean bean = new MatchResultBean();
            bean.channel = "bearever";
            bean.token = "bearever" + System.currentTimeMillis();
            callback.suc(bean);
        }
    }
}
