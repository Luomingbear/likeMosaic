package cn.bearever.likemosaic.home

import android.Manifest
import android.content.Intent
import cn.bearever.likemosaic.Constant
import cn.bearever.likemosaic.R
import cn.bearever.likemosaic.call.VideoCallActivity
import cn.bearever.likemosaic.bean.MatchResultBean
import cn.bearever.mingbase.app.mvp.BaseActivity
import cn.bearever.mingbase.app.permission.AsyncPermission
import cn.bearever.mingbase.app.util.ToastUtil
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_main.*


/**
 *  主页面
 *
 * @author luoming
 * @date 2020/4/11
 */
class HomeActivity : BaseActivity<HomePresenter>(), HomeContact.View {

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initPresenter() {
        mPresenter = HomePresenter(this, this)
    }

    override fun initView() {
        StatusBarUtil.setTransparent(this)
        btn_match.setOnClickListener {
            it.isEnabled = false
            ToastUtil.show("开始匹配")
            btn_match.text = "停止匹配"
            requestPermission()
        }
    }

    private fun requestPermission() {
        AsyncPermission.with(this).requestNoTest(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onAllGranted {
                    //request match
                    mPresenter.requestMatch()
                }
                .onDenied { permissions ->
                    ToastUtil.show("缺少" + permissions[0].message)
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
        ToastUtil.show(msg)
        btn_match.isEnabled = true
        btn_match.setText(R.string.start_match)
    }

    override fun matchSucceed(matchResultBean: MatchResultBean) {
        //todo 匹配成功，进入聊天
        goVideoChat(matchResultBean)
        btn_match.isEnabled = true
        btn_match.setText(R.string.start_match)
    }

    private fun goVideoChat(matchResultBean: MatchResultBean) {
        intent = Intent()
        intent.setClass(this, VideoCallActivity::class.java)
        intent.putExtra(Constant.KEY_MATCH_BEAN, matchResultBean)
        startActivity(intent)
    }


}