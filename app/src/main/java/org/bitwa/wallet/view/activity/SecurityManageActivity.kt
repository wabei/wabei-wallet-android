package org.bitwa.wallet.view.activity

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_security_manage.*
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseActivity
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class SecurityManageActivity : BaseActivity() {
    override val mRootLayoutResId: Int = R.layout.activity_security_manage
    override val mTitleText: String = "安全设置"

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        mSetGesturePasswordBtn.onClick {
            //TODO JiJunpeng created. 设置手势密码
            toast("尚未实现该功能")
        }
        mClearGesturePasswordBtn.onClick {
            //TODO JiJunpeng created. 更新手势密码
            toast("尚未实现该功能")
        }
        mUpdateGesturePasswordBtn.onClick {
            //TODO JiJunpeng created. 修改手势密码
            toast("尚未实现该功能")
        }
    }

    companion object {
        fun show(activity: Activity) {
            activity.startActivity<SecurityManageActivity>()
        }
    }
}
