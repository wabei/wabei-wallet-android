package org.bitwa.wallet.view.activity

import android.app.Activity
import android.os.Bundle
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_receipt.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseActivity
import org.bitwa.wallet.ext.copyToClip
import org.bitwa.wallet.ext.exeSimpSuspend
import org.bitwa.wallet.ext.toGone
import org.bitwa.wallet.ext.toVisible
import org.bitwa.wallet.presenter.WalletPresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

//TODO JiJunpeng created. 复制收款地址
class ReceiptActivity : BaseActivity() {
    override val mRootLayoutResId: Int = R.layout.activity_receipt
    override val mTitleText: String = "收款"

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        mCopyAddressBtn.onClick { copyAddress() }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        val walletId = intent.getIntExtra(KEY_WALLET_ID, -1)
        WalletPresenter.obtainWallet(walletId) {
            this.onSuccess = {
                mAddressTv.text = it.address
                // 显示二维码图片
                createAddressQr(it.address)
            }
            this.onFail = mCommonFailCallback
        }
    }

    private fun createAddressQr(address: String) {
        launch(UI) {
            val bitmap = exeSimpSuspend {
                val sideLength = displayMetrics.widthPixels - dip(100)
                CodeUtils.createImage("wabei:$address", sideLength, sideLength, null)
            }
            mQrCodeIv.layoutParams.height = bitmap.height
            mQrCodeIv.layoutParams = mQrCodeIv.layoutParams
            mQrCodeIv.imageBitmap = bitmap
            mQrCodeIv.toVisible()
            mWaitingPb.toGone()
        }
    }

    private fun copyAddress() {
        mAddressTv.text.toString().copyToClip(act)
        toast("已经复制到剪切板")
    }

    companion object {
        private const val KEY_WALLET_ID = "KEY_WALLET_ID"
        fun show(activity: Activity, walletId: Int) {
            activity.startActivity<ReceiptActivity>(KEY_WALLET_ID to walletId)
        }
    }
}
