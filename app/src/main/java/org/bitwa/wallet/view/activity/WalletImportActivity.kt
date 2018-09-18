package org.bitwa.wallet.view.activity

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_wallet_import.*
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseActivity
import org.bitwa.wallet.view.adapter.WalletImportPagerAdapter
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

//TODO JiJunpeng created. 通过扫码导入钱包
class WalletImportActivity : BaseActivity() {
    override val mRootLayoutResId: Int = R.layout.activity_wallet_import
    override val mTitleText: String = "导入钱包"

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        mViewPager.adapter = WalletImportPagerAdapter(supportFragmentManager)
        mTabLayout.setupWithViewPager(mViewPager)
    }

    companion object {
        fun show(activity: Activity) {
            activity.startActivity<WalletImportActivity>()
        }

        fun showForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult<WalletImportActivity>(requestCode)
        }
    }
}
