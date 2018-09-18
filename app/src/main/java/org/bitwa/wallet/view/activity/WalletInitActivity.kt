package org.bitwa.wallet.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_wallet_init.*
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseActivity
import org.jetbrains.anko.act
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity

class WalletInitActivity : BaseActivity() {
    override val mRootLayoutResId: Int = R.layout.activity_wallet_init
    override val mHideBackBtn: Boolean = true

    override fun initWindow(savedInstanceState: Bundle?) {
        super.initWindow(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        mImportWalletBtn.onClick { WalletImportActivity.showForResult(act, 1) }
        mCreateWalletBtn.onClick { WalletCreateActivity.showForResult(act, 1) }
    }

    private fun gotoWalletActivity() {
        MainActivity.show(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                gotoWalletActivity()
                finish()
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        fun show(activity: Activity) {
            activity.startActivity<WalletInitActivity>()
        }
    }
}
