package org.bitwa.wallet.view.activity

import android.os.Bundle
import android.view.WindowManager
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseActivity
import org.bitwa.wallet.service.WalletService
import org.jetbrains.anko.act

class SplashActivity : BaseActivity() {
    override val mRootLayoutResId: Int = R.layout.activity_splash_2

    override fun initWindow(savedInstanceState: Bundle?) {
        super.initWindow(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        startGotoNext()
    }

    private fun startGotoNext() {
        launch(UI) {
            delay(3000)
            gotoNext()
            finish()
        }
    }

    private fun gotoNext() {
        if (WalletService.hasWallet()) {
            MainActivity.show(act)
        } else {
            WalletInitActivity.show(act)
        }
    }
}
