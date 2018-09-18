package org.bitwa.wallet.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.cell_appbar.*
import org.bitwa.wallet.R
import org.bitwa.wallet.ext.toGone
import org.bitwa.wallet.view.dialog.WaitingDialog
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast

/**
 * @author jijunpeng created on 2018/7/3.
 */
@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity() {

    abstract val mRootLayoutResId: Int
    open val mTitleText: String? = null
    open val mHideBackBtn: Boolean = false

    private val mWaitingDialog by lazy { WaitingDialog(ctx) }
    protected val mCommonFailCallback by lazy { { errMsg: String -> toast(errMsg);Unit } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow(savedInstanceState)
        setContentView(mRootLayoutResId)
        initWidget(savedInstanceState)
        initData(savedInstanceState)
    }

    open fun initData(savedInstanceState: Bundle?) {}
    open fun initWindow(savedInstanceState: Bundle?) {}
    open fun initWidget(savedInstanceState: Bundle?) {
        //如果复写标题，则重新设置标题
        mTitleText?.also { findViewById<TextView>(R.id.mTitleTv)?.text = it }
        //判断是否需要隐藏返回按键
        if (mHideBackBtn) {
            mBackBtn?.toGone()
        }
    }

    protected fun showWaitingDialog() {
        mWaitingDialog.show()
    }

    protected fun hideWaitDialog() {
        mWaitingDialog.dismiss()
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments?.forEach {
            if ((it as? BaseFragment)?.onBackPressed() == true) {
                return
            }
        }
        if (mWaitingDialog.isShowing) {
            mWaitingDialog.dismiss()
            return
        }
        super.onBackPressed()
        finish()
    }

    fun onBack(view: View) {
        onBackPressed()
    }
}