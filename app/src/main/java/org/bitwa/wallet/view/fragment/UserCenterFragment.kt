package org.bitwa.wallet.view.fragment

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_user_center.*
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseFragment
import org.bitwa.wallet.ext.toGone
import org.bitwa.wallet.view.activity.SecurityManageActivity
import org.bitwa.wallet.view.activity.WalletManageActivity
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.act

/**
 * @author jijunpeng created on 2018/7/8.
 */
class UserCenterFragment : BaseFragment() {
    override val mRootLayoutResId: Int = R.layout.fragment_user_center

    override fun initWidget(view: View, bundle: Bundle?) {
        super.initWidget(view, bundle)
        initVersionText()
        mWalletManageBtn.onClick { WalletManageActivity.show(act) }
        mSecurityBtn.onClick { SecurityManageActivity.show(act) }
        //TODO JiJunpeng created. 增加安全管理功能
        mSecurityBtn.toGone()
    }

    private fun initVersionText() {
        val packageInfo = this.context?.packageManager?.getPackageInfo(this.context?.packageName, 0)
        mVersionTv.text = packageInfo?.versionName + "-" + packageInfo?.versionCode
    }
}