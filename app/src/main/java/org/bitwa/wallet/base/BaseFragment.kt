package org.bitwa.wallet.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.bitwa.wallet.view.dialog.WaitingDialog
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.toast

/**
 * @author jijunpeng created on 2018/7/3.
 */
abstract class BaseFragment : Fragment() {

    private var mRootView: View? = null
    protected abstract val mRootLayoutResId: Int
    private var mIsFirstInitData = true
    private val mWaitingDialog by lazy { WaitingDialog(ctx) }
    protected val mCommonFailCallback by lazy { { errMsg: String -> toast(errMsg);Unit } }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        initArgs(arguments)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = mRootView
        if (rootView == null) {
            val layId = mRootLayoutResId
            val inflateView = inflater.inflate(layId, container, false)
            inflateView.isClickable = true
            mRootView = inflateView
            rootView = inflateView
        } else {
            (rootView.parent as? ViewGroup)?.also { it.removeView(rootView) }
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mIsFirstInitData) {
            initWidget(view, savedInstanceState)
            // 触发一次以后就不会触发
            mIsFirstInitData = false
            // 触发
            initData(view, savedInstanceState)
        }
        // 当View创建完成后初始化数据
        initDataRepeat(view, savedInstanceState)
    }

    protected fun showWaitingDialog() {
        mWaitingDialog.show()
    }

    protected fun hideWaitDialog() {
        mWaitingDialog.dismiss()
    }

    /**
     * 初始化相关参数
     */
    protected open fun initArgs(bundle: Bundle?) {}

    /**
     * 初始化控件
     */
    protected open fun initWidget(view: View, bundle: Bundle?) {}

    /**
     * 当首次初始化数据的时候会调用的方法
     */
    protected open fun initData(view: View, bundle: Bundle?) {}

    /**
     * 初始化数据，无论是否是首次初始化，都会调用该方法
     */
    protected open fun initDataRepeat(view: View, bundle: Bundle?) {}

    /**
     * 返回按键触发时调用
     *
     * @return true Activity不会处理onBackPressed事件； false Activity会处理onBackPressed事件
     */
    open fun onBackPressed(): Boolean {
        if (mWaitingDialog.isShowing) {
            mWaitingDialog.dismiss()
            return true
        }
        return false
    }

    fun onBack(view: View) {
        onBackPressed()
    }
}