package cn.bearever.likemosaic.home

import android.Manifest
import android.content.Intent
import cn.bearever.likemosaic.Constant
import cn.bearever.likemosaic.R
import cn.bearever.likemosaic.VideoChatViewActivity
import cn.bearever.likemosaic.bean.MatchResultBean
import cn.bearever.likemosaic.bean.TopicBean
import cn.bearever.mingbase.app.mvp.BaseActivity
import cn.bearever.mingbase.app.permission.AsyncPermission
import cn.bearever.mingbase.app.util.ToastUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


/**
 *  主页面
 *
 * @author luoming
 * @date 2020/4/11
 */
class MainActivity : BaseActivity<HomePresenter>(), HomeContact.View {

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initPresenter() {
        mPresenter = HomePresenter(this, this)
    }

    override fun initView() {
        btn_match.setOnClickListener {
            requestPermission()
        }
    }

    private fun requestPermission() {
        AsyncPermission.with(this).request(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onAllGranted {
                    //request match
                    mPresenter.requestMatch()
                }
                .onDenied { permissions ->
                    ToastUtil.show(applicationContext, "缺少" + permissions[0].message)

                }
    }

    override fun startMatch() {
        //todo 开始匹配的动画
    }

    override fun stopMatch() {
        //todo 回退到默认状态的动画
    }

    override fun matchFailed(msg: String) {
        //todo 匹配失败
        ToastUtil.show(this, msg)
    }

    override fun matchSucceed(matchResultBean: MatchResultBean) {
        //todo 匹配成功，进入聊天
        goVideoChat(matchResultBean.channel)
    }

    private fun goVideoChat(channel: String) {
        intent = Intent()
        intent.setClass(this, VideoChatViewActivity::class.java)
        intent.putExtra(Constant.KEY_CHANNEL, channel)
        val list = ArrayList<TopicBean>()
        for (i in 0..8) {
            val bean = TopicBean(id = i)
            list.add(bean)
        }
        intent.putExtra(Constant.KEY_TOPIC_LIST, list)
        startActivity(intent)
    }


}