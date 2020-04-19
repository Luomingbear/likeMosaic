package cn.bearever.likemosaic.home

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.View
import androidx.core.animation.addListener
import cn.bearever.likemosaic.Constant
import cn.bearever.likemosaic.R
import cn.bearever.likemosaic.call.VideoCallActivity
import cn.bearever.likemosaic.bean.MatchResultBean
import cn.bearever.mingbase.app.mvp.BaseActivity
import cn.bearever.mingbase.app.permission.AsyncPermission
import cn.bearever.mingbase.app.util.DipPxUtil
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
    private lateinit var animationStart: ValueAnimator
    private lateinit var animationStop: ValueAnimator
    private var mOtherPositionStart = 0F
    private var mMePositionStart = 0F
    private var mLoadingPositionStart = 0F
    private var mBtnPositionStart = 0F

    private var mOtherPositionEnd = 0F
    private var mMePositionEnd = 0F
    private var mLoadingPositionEnd = 0F
    private var mBtnPositionEnd = 0F

    companion object {
        private val TAG = "HomeActivity"
    }

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
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                requestPermission()
            } else {
                mPresenter?.stopMatch()
                stopMatch()
            }
        }
        relativeLayout.post {
            setupInitLayout()
        }
    }

    private fun setupInitLayout() {
        setupAnimationPosition()
        fl_remote_container.y = mOtherPositionStart
        iv_loading.y = mLoadingPositionStart
        fl_mine_container.y = mMePositionStart
        btn_match.y = mBtnPositionStart
        mMePositionStart = fl_mine_container.y
    }


    private fun requestPermission() {
        AsyncPermission.with(this).requestNoTest(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onAllGranted {
                    //request match
                    startMatch()
                }
                .onDenied { permissions ->
                    ToastUtil.show("缺少" + permissions[0].message)
                    btn_match.isEnabled = true
                }
    }

    override fun startMatch() {
        btn_match.isSelected = true
        if (!this::animationStart.isInitialized) {
            animationStart = ObjectAnimator.ofFloat(0F, 100F)
            animationStart.duration = 500
            animationStart.addListener(onEnd = {
                val drawable = iv_loading.drawable as AnimationDrawable
                drawable.start()
                mPresenter.requestMatch()
                btn_match.isEnabled = true
                btn_match.text = "停止匹配"
            })

            animationStart.addUpdateListener {
                val percent = it.animatedValue as Float / 100F
                Log.d(TAG, "vvvvvvvv:" + percent)
                //计算按钮的位置
                //对方的画面
                val y1 = mOtherPositionStart + (mOtherPositionEnd - mOtherPositionStart) * percent
                fl_remote_container.y = y1
                //我的画面
                val y2 = mMePositionStart + (mMePositionEnd - mMePositionStart) * percent
                fl_mine_container.y = y2
                //匹配按钮
                val y3 = mBtnPositionStart + (mBtnPositionEnd - mBtnPositionStart) * percent
                btn_match.y = y3
                //loading图标
                val y4 = mLoadingPositionStart + (mLoadingPositionEnd - mLoadingPositionStart) * percent
                iv_loading.y = y4
            }
        }
        animationStart.cancel()
        animationStart.start()
    }

    private fun setupAnimationPosition() {
        if (mOtherPositionStart == 0F) {
            relativeLayout.measure(0, 0)
            mOtherPositionStart = (-fl_remote_container.measuredHeight).toFloat()
            mMePositionStart = relativeLayout.measuredHeight / 2F
            mLoadingPositionStart = (-DipPxUtil.dip2px(14F)).toFloat()
            mBtnPositionStart = mMePositionStart + fl_mine_container.measuredHeight + DipPxUtil.dip2px(50F)

            mOtherPositionEnd = relativeLayout.measuredHeight / 4F
            mMePositionEnd = relativeLayout.measuredHeight * 3 / 4F
            mLoadingPositionEnd = mOtherPositionEnd + fl_remote_container.measuredHeight + (mMePositionEnd - (mMePositionStart)) / 2F - iv_loading.measuredHeight
            mBtnPositionEnd = mMePositionEnd + fl_mine_container.measuredHeight + DipPxUtil.dip2px(50F)
        }
    }

    override fun stopMatch() {
        btn_match.isSelected = false
        if (!this::animationStop.isInitialized) {
            animationStop = ObjectAnimator.ofFloat(100F, 0F)
            animationStop.duration = 500
            animationStop.addListener(onEnd = {
                btn_match.isEnabled = true
                val drawable = iv_loading.drawable as AnimationDrawable
                drawable.stop()
                btn_match.setText(R.string.start_match)
            })

            animationStop.addUpdateListener {
                val percent = it.animatedValue as Float / 100F
                //计算按钮的位置
                //对方的画面
                val y1 = mOtherPositionStart + (mOtherPositionEnd - mOtherPositionStart) * percent
                fl_remote_container.y = y1
                //我的画面
                val y2 = mMePositionStart + (mMePositionEnd - mMePositionStart) * percent
                fl_mine_container.y = y2
                //匹配按钮
                val y3 = mBtnPositionStart + (mBtnPositionEnd - mBtnPositionStart) * percent
                btn_match.y = y3
                //loading图标
                val y4 = mLoadingPositionStart + (mLoadingPositionEnd - mLoadingPositionStart) * percent
                iv_loading.y = y4
            }
        }
        animationStop.cancel()
        animationStop.start()
    }

    override fun matchFailed(msg: String) {
        ToastUtil.show(msg)
        btn_match.isEnabled = false
        stopMatch()
    }

    override fun matchSucceed(matchResultBean: MatchResultBean) {
        goVideoChat(matchResultBean)
        btn_match.isEnabled = true
        btn_match.setText(R.string.start_match)
    }

    private fun goVideoChat(matchResultBean: MatchResultBean) {
        intent = Intent()
        intent.setClass(this, VideoCallActivity::class.java)
        intent.putExtra(Constant.KEY_MATCH_BEAN, matchResultBean)
        startActivity(intent)
        //
        setupInitLayout()
        btn_match.isSelected = false
    }

}